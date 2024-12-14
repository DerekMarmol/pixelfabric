package com.pixelfabric.item.custom;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class RegenerationTotemItem extends Item {
    private static final int EFFECT_DURATION = 89; // Un poco menos que 4.5 segundos
    private static final int EFFECT_AMPLIFIER = 0; // Regeneración nivel 1 (0-based)
    private static final int CHECK_INTERVAL = 80; // 4 segundos

    public RegenerationTotemItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof PlayerEntity player)) {
            return;
        }

        // Solo aplicar el efecto cada CHECK_INTERVAL ticks
        if (world.getTime() % CHECK_INTERVAL != 0) {
            return;
        }

        boolean inMainHand = player.getMainHandStack().getItem() == this;
        boolean inOffHand = player.getOffHandStack().getItem() == this;

        if (inMainHand || inOffHand) {
            // Verificar si el jugador necesita regeneración
            if (player.getHealth() < player.getMaxHealth()) {
                // Aplicar el efecto de regeneración
                StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.REGENERATION);

                // Solo aplicar si no tiene el efecto o está por terminar
                if (currentEffect == null || currentEffect.getDuration() <= 20) {
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.REGENERATION,
                            EFFECT_DURATION,
                            EFFECT_AMPLIFIER,
                            false,  // ambient
                            true,   // visible
                            true    // mostrar partículas
                    ));
                }
            }
        }
    }
}