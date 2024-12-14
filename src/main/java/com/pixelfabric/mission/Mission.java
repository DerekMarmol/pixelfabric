package com.pixelfabric.mission;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface Mission {
    boolean checkCompletion(PlayerEntity player);
    void giveReward(PlayerEntity player);
    String getDescription();

    default int getRequiredKills() {
        return 0;
    }

    default void incrementKills(ServerPlayerEntity player) {
    }
}