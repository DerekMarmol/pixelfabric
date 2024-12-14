package com.pixelfabric.events;

import com.pixelfabric.data.Components;
import com.pixelfabric.data.IPlayerHealthData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModEvents {
    public static void register() {
        // Evento para cuando el jugador respawnea
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            updatePlayerHealth(newPlayer);
        });

        // Evento para cuando el jugador se conecta
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            updatePlayerHealth(player);
        });

        // Evento para copiar datos entre dimensiones
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            updatePlayerHealth(newPlayer);
        });
    }

    private static void updatePlayerHealth(ServerPlayerEntity player) {
        EntityAttributeInstance healthAttribute =
                player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);

        if (healthAttribute != null) {
            IPlayerHealthData healthData = Components.PLAYER_HEALTH.get(player);
            int extraHearts = healthData.getExtraHearts();

            if (extraHearts > 0) {
                healthAttribute.setBaseValue(20.0D + extraHearts);
                player.setHealth(player.getMaxHealth());
            }
        }
    }
}