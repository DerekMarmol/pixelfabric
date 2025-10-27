// Archivo: effects/custom/InvertedControlsEffect.java
package com.pixelfabric.effects.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class InvertedControlsEffect extends StatusEffect {

    public InvertedControlsEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Se ejecuta cada tick mientras el efecto esté activo
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Solo mostrar mensaje cada 2 segundos (40 ticks)
        if (entity instanceof ServerPlayerEntity && entity.age % 40 == 0) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            var statusEffect = player.getStatusEffect(this);
            if (statusEffect != null) {
                int remainingSeconds = (int) Math.ceil(statusEffect.getDuration() / 20.0);

                if (remainingSeconds > 0) {
                    player.sendMessage(
                            Text.literal("§c¡Controles invertidos! " + remainingSeconds + "s restantes"),
                            true // Mostrar en actionbar
                    );
                }
            }
        }
    }

    // Método cuando el efecto se aplica por primera vez (opcional)
    public void onEffectApplied(LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.sendMessage(
                    Text.literal("§c¡Una bruja corrupta ha invertido tus controles!"),
                    false
            );
        }
    }

    // Método cuando el efecto se elimina (opcional)
    public void onEffectRemoved(LivingEntity entity) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.sendMessage(
                    Text.literal("§a¡Tus controles han vuelto a la normalidad!"),
                    true
            );
        }
    }
}