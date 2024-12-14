package com.pixelfabric.commands;

import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class SpiderCeilingMechanic {
    private static final String COMMAND_NAME = "togglespiderceiling";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);
                        String status = isActive ? "activado" : "desactivado";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Arañas en techos " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isSpiderCeilingActive() {
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }
}