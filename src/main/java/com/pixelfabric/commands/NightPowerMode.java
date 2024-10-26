package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class NightPowerMode {
    private static boolean nightPowerModeActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglepowerfulnights")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        nightPowerModeActive = !nightPowerModeActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Night Power Mode " + (nightPowerModeActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isNightPowerModeActive() {
        return nightPowerModeActive;
    }
}

