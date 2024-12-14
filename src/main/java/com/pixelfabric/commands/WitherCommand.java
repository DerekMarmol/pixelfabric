package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class WitherCommand {
    private static boolean skeletonWitherActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggleskeletonwither")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        skeletonWitherActive = !skeletonWitherActive;
                        String status = skeletonWitherActive ? "activado" : "desactivado";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Disparo de calaveras wither " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isSkeletonWitherActive() {
        return skeletonWitherActive;
    }
}