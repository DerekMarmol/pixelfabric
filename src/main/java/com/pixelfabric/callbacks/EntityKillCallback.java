package com.pixelfabric.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;

public interface EntityKillCallback {
    Event<EntityKillCallback> EVENT = EventFactory.createArrayBacked(EntityKillCallback.class,
            (listeners) -> (player, entityType) -> {
                for (EntityKillCallback listener : listeners) {
                    listener.onEntityKill(player, entityType);
                }
            });

    void onEntityKill(ServerPlayerEntity player, EntityType<?> entityType);
}