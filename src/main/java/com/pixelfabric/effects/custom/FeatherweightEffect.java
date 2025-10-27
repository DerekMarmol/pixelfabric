// Archivo: effects/custom/FeatherweightEffect.java
package com.pixelfabric.effects.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FeatherweightEffect extends StatusEffect {

    public FeatherweightEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Se ejecuta cada tick mientras el efecto está activo
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Solo mostrar mensaje cada 3 segundos (60 ticks) para no spamear
        if (entity instanceof ServerPlayerEntity && entity.age % 60 == 0) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            var statusEffect = player.getStatusEffect(this);
            if (statusEffect != null) {
                int remainingSeconds = (int) Math.ceil(statusEffect.getDuration() / 20.0);
            }
        }
    }


    // Método estático para verificar si una entidad tiene el efecto
    public static boolean hasFeatherweight(LivingEntity entity) {
        // Necesitarás importar ModEffects cuando lo registres
        return entity.hasStatusEffect(com.pixelfabric.effects.ModEffects.FEATHERWEIGHT_EFFECT);
    }
}