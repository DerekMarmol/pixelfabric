package com.pixelfabric.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerData {
    private static final String PLAYER_HEARTS_KEY = "pixelfabric.extra_hearts";

    public static int getExtraHearts(ServerPlayerEntity player) {
        NbtCompound persistentData = player.writeNbt(new NbtCompound());
        return persistentData.contains(PLAYER_HEARTS_KEY) ? persistentData.getInt(PLAYER_HEARTS_KEY) : 0;
    }

    public static void setExtraHearts(ServerPlayerEntity player, int hearts) {
        NbtCompound persistentData = player.writeNbt(new NbtCompound());
        persistentData.putInt(PLAYER_HEARTS_KEY, hearts);
        player.readNbt(persistentData);
    }
}