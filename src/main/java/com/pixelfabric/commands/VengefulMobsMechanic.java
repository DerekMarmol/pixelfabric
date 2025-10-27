package com.pixelfabric.commands;

import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class VengefulMobsMechanic {
    private static final String COMMAND_NAME = "togglevengefulmobs";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);
                        String status = isActive ? "activados" : "desactivados";
                        context.getSource().sendFeedback(
                                () -> Text.literal("§4Mobs vengativos " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isVengefulMobsActive() {
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }
}