package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;


public class NightmareMode {
    private static boolean nightmareModeActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglenightmaremode")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        nightmareModeActive = !nightmareModeActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Nightmare Mode " + (nightmareModeActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isNightmareModeActive() {
        return nightmareModeActive;
    }
}
