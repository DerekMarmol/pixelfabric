package com.pixelfabric.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class CreeperFlashEffect {
    private static int flashDuration = 0;
    private static final int CREEPER_FLASH_DURATION = 120; // 3 segundos
    public static final Identifier CREEPER_FLASH_PACKET_ID = new Identifier("pixelfabric", "creeper_flash");

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(CREEPER_FLASH_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                flashDuration = CREEPER_FLASH_DURATION;
            });
        });
    }

    public static void clientTick() {
        if (flashDuration > 0) {
            flashDuration--;
        }
    }

    public static void render(MatrixStack matrices, float tickDelta) {
        if (flashDuration > 0) {
            MinecraftClient client = MinecraftClient.getInstance();
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);

            float alpha = (float) flashDuration / CREEPER_FLASH_DURATION;

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(0, height, 0).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(width, height, 0).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(width, 0, 0).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(0, 0, 0).color(1f, 1f, 1f, alpha).next();
            Tessellator.getInstance().draw();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }
}