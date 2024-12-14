package com.pixelfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FlashEffectPacket {
    public static final Identifier FLASH_EFFECT_PACKET_ID = new Identifier("tumod", "flash_effect");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(FLASH_EFFECT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            // El servidor no necesita manejar nada aquí
        });
    }

    public static void send(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLASH_EFFECT_PACKET_ID, buf);
    }
}