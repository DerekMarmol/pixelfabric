package com.pixelfabric.commands;

import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class SpiderAggressionMechanic {
    private static final String COMMAND_NAME = "togglespideraggression";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);
                        String status = isActive ? "activada" : "desactivada";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Agresión de arañas " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isSpiderAggressionActive() {
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }
}