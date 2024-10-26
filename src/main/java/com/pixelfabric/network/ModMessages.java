package com.pixelfabric.network;

import com.pixelfabric.entity.custom.SkullEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier SPACE_PRESS_ID = new Identifier("pixelfabric", "space_press");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SPACE_PRESS_ID, (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readInt();

            server.execute(() -> {
                Entity entity = player.getWorld().getEntityById(entityId);
                if (entity instanceof SkullEntity skull) {
                    skull.handleSpacePress();
                }
            });
        });
    }

    public static PacketByteBuf createSpacePressPacket(int entityId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        return buf;
    }
}