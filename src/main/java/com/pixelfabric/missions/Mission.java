package com.pixelfabric.missions;

import net.minecraft.entity.player.PlayerEntity;

public interface Mission {
    boolean checkCompletion(PlayerEntity player);
    void giveReward(PlayerEntity player);
    String getDescription();
}
