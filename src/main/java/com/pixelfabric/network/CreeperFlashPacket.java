package com.pixelfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import com.pixelfabric.client.CreeperFlashEffect;

public class CreeperFlashPacket {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(CreeperFlashEffect.CREEPER_FLASH_PACKET_ID,
                (server, player, handler, buf, responseSender) -> {});
    }

    public static void send(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, CreeperFlashEffect.CREEPER_FLASH_PACKET_ID, PacketByteBufs.create());
    }
}
