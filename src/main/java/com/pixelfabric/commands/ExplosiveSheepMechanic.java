package com.pixelfabric.commands;

import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ExplosiveSheepMechanic {
    private static final String COMMAND_NAME = "toggleexplosivesheep";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);
                        String status = isActive ? "activadas" : "desactivadas";
                        context.getSource().sendFeedback(
                                () -> Text.literal("§eOvejas explosivas " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isExplosiveSheepActive() {
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }
}