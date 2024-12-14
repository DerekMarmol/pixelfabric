package com.pixelfabric.mixin;

import com.pixelfabric.item.custom.HeartTotemItem;
import com.pixelfabric.item.custom.RegenerationTotemItem;
import com.pixelfabric.item.custom.InventoryTotemItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Hand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.stat.Stats;
import net.minecraft.advancement.criterion.Criteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityTotem {
    @Inject(
            method = "tryUseTotem", // Método que se ejecuta cuando una entidad intenta usar un totem
            at = @At("HEAD"),       // Inyectamos al inicio del método
            cancellable = true      // Permitimos cancelar el método original si encontramos nuestro totem
    )
    private void onTryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Si el daño bypasea la invulnerabilidad (como el comando /kill), no intentamos salvarlo
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        ItemStack totemStack = null;
        Hand totemHand = null;

        // SECCIÓN 1: BÚSQUEDA DEL TOTEM
        // Primero buscamos en las manos para los totems normales
        for (Hand hand : Hand.values()) {
            ItemStack stack = entity.getStackInHand(hand);
            if (stack.getItem() instanceof HeartTotemItem ||
                    stack.getItem() instanceof RegenerationTotemItem) {
                totemStack = stack;
                totemHand = hand;
                break;
            }
        }

        // Si no encontramos un totem en las manos y la entidad es un jugador,
        // buscamos el InventoryTotem en todo el inventario
        if (totemStack == null && entity instanceof PlayerEntity player) {
            // Buscamos en todo el inventario del jugador
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() instanceof InventoryTotemItem) {
                    totemStack = stack;
                    break;
                }
            }
        }

        // SECCIÓN 2: APLICACIÓN DE EFECTOS
        if (totemStack != null) {
            // Consumimos el totem
            totemStack.decrement(1);

            // Efectos base que comparten todos los totems
            entity.setHealth(1.0F);
            entity.clearStatusEffects();
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            
            // SECCIÓN 3: EFECTOS ESPECÍFICOS POR TIPO DE TOTEM
            if (totemStack.getItem() instanceof HeartTotemItem) {
                // Efectos del Heart Totem: regeneración fuerte y absorción
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            }
            else if (totemStack.getItem() instanceof RegenerationTotemItem) {
                // Efectos del Regeneration Totem: regeneración muy fuerte y más absorción
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1200, 2));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200, 2));
            }
            else if (totemStack.getItem() instanceof InventoryTotemItem) {
                // Efectos del Inventory Totem: similar al totem vanilla
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            }

            // SECCIÓN 4: EFECTOS VISUALES Y ESTADÍSTICAS
            // Activar efectos visuales (partículas y sonido)
            entity.getWorld().sendEntityStatus(entity, (byte)35);

            // Si es un jugador, actualizamos sus estadísticas
            if (entity instanceof ServerPlayerEntity player) {
                player.incrementStat(Stats.USED.getOrCreateStat(totemStack.getItem()));
                Criteria.USED_TOTEM.trigger(player, totemStack);
            }

            // Indicamos que el totem se usó exitosamente
            cir.setReturnValue(true);
        }
    }
}