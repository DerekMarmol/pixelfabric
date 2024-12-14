package com.pixelfabric.events;

import com.pixelfabric.mission.KillTracker;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class KillEventHandler {
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            // Verificar si la muerte fue causada por un jugador
            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                // Si la entidad muerta es una LivingEntity (mobs, animales, etc)
                if (entity instanceof LivingEntity) {
                    // Registrar la kill en nuestro sistema
                    KillTracker.trackKill(player, entity.getType());
                }
            }
        });
    }
}