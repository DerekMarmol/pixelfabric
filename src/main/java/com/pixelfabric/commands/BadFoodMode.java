package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class BadFoodMode {
    private static boolean badFoodModeActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglebadfood")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        badFoodModeActive = !badFoodModeActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Bad Food Mode " + (badFoodModeActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isBadFoodModeActive() {
        return badFoodModeActive;
    }

    public static boolean shouldFoodGiveNegativeEffect() {
        return badFoodModeActive && Math.random() < 0.1;
    }
}

