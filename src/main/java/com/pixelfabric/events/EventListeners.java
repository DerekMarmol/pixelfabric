package com.pixelfabric.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import static com.pixelfabric.events.CustomEventSystem.events;
import static com.pixelfabric.events.CustomEventSystem.executeEvent;

public class EventListeners {
    public static void register() {
        // Listener para muerte de jugador
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            for (CustomEventSystem.CustomEvent event : events.values()) {
                if (event.type.equalsIgnoreCase("DEATH")) {
                    executeEvent(event.name, newPlayer);
                }
            }
        });

        // Listener para uso de tótem
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damage) -> {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                ItemStack mainHand = player.getMainHandStack();
                ItemStack offHand = player.getOffHandStack();

                if (mainHand.isOf(Items.TOTEM_OF_UNDYING) || offHand.isOf(Items.TOTEM_OF_UNDYING)) {
                    for (CustomEventSystem.CustomEvent event : events.values()) {
                        if (event.type.equalsIgnoreCase("TOTEM")) {
                            executeEvent(event.name, player);
                        }
                    }
                }
            }
            return true;
        });

        // Listener para unirse al servidor
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            for (CustomEventSystem.CustomEvent event : events.values()) {
                if (event.type.equalsIgnoreCase("JOIN")) {
                    executeEvent(event.name, player);
                }
            }
        });
    }
}