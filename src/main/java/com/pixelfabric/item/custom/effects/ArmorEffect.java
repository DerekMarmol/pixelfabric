package com.pixelfabric.item.custom.effects;

import net.minecraft.entity.player.PlayerEntity;

public interface ArmorEffect {
    void applyEffect(PlayerEntity player);
    void removeEffect(PlayerEntity player);
}
