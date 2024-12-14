package com.pixelfabric.updater;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ModAutoUpdaterClient {
    public static final String UPDATE_AVAILABLE_MESSAGE = "§PPIXELFABRIC_UPDATE_AVAILABLE§";

    public static void initialize() {
        registerMessageHandler();
    }

    private static void registerMessageHandler() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String msg = message.getString();
            if (msg.startsWith(UPDATE_AVAILABLE_MESSAGE)) {
                String version = msg.replace(UPDATE_AVAILABLE_MESSAGE, "");
                showUpdateNotification(version);
            }
        });
    }

    private static void showUpdateNotification(String version) {
        MinecraftClient client = MinecraftClient.getInstance();
        SystemToast.add(
                client.getToastManager(),
                SystemToast.Type.TUTORIAL_HINT,
                Text.literal("Nueva versión disponible: " + version),
                Text.literal("Se instalará al cerrar el juego")
        );
    }
}