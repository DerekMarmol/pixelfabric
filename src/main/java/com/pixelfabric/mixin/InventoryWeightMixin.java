package com.pixelfabric.mixin;

import com.pixelfabric.commands.InventoryWeightMechanic;
import com.pixelfabric.effects.custom.FeatherweightEffect;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class InventoryWeightMixin {

    @Unique
    private static final UUID WEIGHT_SPEED_MODIFIER_UUID = UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635");

    @Unique
    private int lastInventoryWeight = -1;

    @Unique
    private int weightWarningTimer = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void applyInventoryWeight(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // Solo aplicar en el servidor
        if (player.getWorld().isClient) {
            return;
        }

        // Solo si la mecánica está activa
        if (!InventoryWeightMechanic.isInventoryWeightActive()) {
            removeWeightModifier(player);
            return;
        }

        // No afectar jugadores en creativo o espectador
        if (player.isCreative() || player.isSpectator()) {
            removeWeightModifier(player);
            return;
        }

        // Verificar si tiene efecto Peso Pluma
        if (FeatherweightEffect.hasFeatherweight(player)) {
            removeWeightModifier(player);
            return;
        }

        // Calcular peso cada 10 ticks (0.5 segundos)
        if (player.age % 10 == 0) {
            int currentWeight = calculateInventoryWeight(player.getInventory());

            // Aplicar siempre (aunque no cambie de rango)
            applyWeightModifier(player, currentWeight);

            // Solo mostrar advertencia cada 5 segundos si está muy cargado
            if (currentWeight > 60 && player instanceof ServerPlayerEntity) {
                weightWarningTimer++;
                if (weightWarningTimer >= 100) { // 5 segundos
                    weightWarningTimer = 0;
                    ((ServerPlayerEntity) player).sendMessage(
                            Text.literal("§6⚠ Tu inventario está pesado! Slots ocupados: " + currentWeight + "%"),
                            true
                    );
                }
            }

            lastInventoryWeight = currentWeight;
        }
    }

    @Unique
    private int calculateInventoryWeight(PlayerInventory inventory) {
        int occupiedSlots = 0;
        int totalSlots = 41; // 36 inventario + 4 armadura + 1 offhand

        // Inventario principal
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);
            if (!stack.isEmpty()) {
                occupiedSlots++;
            }
        }

        // Armadura
        for (int i = 0; i < inventory.armor.size(); i++) {
            ItemStack stack = inventory.armor.get(i);
            if (!stack.isEmpty()) {
                occupiedSlots++;
            }
        }

        // Offhand
        if (!inventory.offHand.get(0).isEmpty()) {
            occupiedSlots++;
        }

        // Calcular porcentaje
        return (occupiedSlots * 100) / totalSlots;
    }

    @Unique
    private void applyWeightModifier(PlayerEntity player, int weight) {
        EntityAttributeInstance speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute == null) return;

        // Remover modificador anterior
        speedAttribute.removeModifier(WEIGHT_SPEED_MODIFIER_UUID);

        if (weight > 0) {
            double speedPenalty = 0.0;

            if (weight > 75) {
                speedPenalty = -0.5; // -50% velocidad
            } else if (weight > 50) {
                speedPenalty = -0.3; // -30% velocidad
            } else if (weight > 25) {
                speedPenalty = -0.15; // -15% velocidad
            }

            if (speedPenalty < 0) {
                EntityAttributeModifier modifier = new EntityAttributeModifier(
                        WEIGHT_SPEED_MODIFIER_UUID,
                        "Inventory weight penalty",
                        speedPenalty,
                        EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                );
                // Usar temporal en lugar de persistente
                speedAttribute.addTemporaryModifier(modifier);

                // Debug para verificar en consola
                System.out.println("Peso actual: " + weight + "% -> penalización: " + speedPenalty);
            }
        }
    }

    @Unique
    private void removeWeightModifier(PlayerEntity player) {
        EntityAttributeInstance speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(WEIGHT_SPEED_MODIFIER_UUID);
        }
        lastInventoryWeight = -1;
        weightWarningTimer = 0;
    }
}
