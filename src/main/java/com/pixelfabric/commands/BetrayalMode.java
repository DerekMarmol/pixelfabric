package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class BetrayalMode {
    private static boolean betrayalModeActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglebetrayalmode")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        betrayalModeActive = !betrayalModeActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Betrayal Mode " + (betrayalModeActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isBetrayalModeActive() {
        return betrayalModeActive;
    }
}
