package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class SkeletonSun {
    private static boolean skeletonSunActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggleskeletonsun")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        skeletonSunActive = !skeletonSunActive;
                        String status = skeletonSunActive ? "activada" : "desactivada";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Cambio de configuración 1/5: Los esqueletos ahora son inmunes a la luz solar. Estado del cambio: " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isSkeletonSunActive() {
        return skeletonSunActive;
    }
}