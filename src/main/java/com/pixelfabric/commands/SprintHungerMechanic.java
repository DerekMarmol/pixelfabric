package com.pixelfabric.commands;

import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class SprintHungerMechanic {
    private static final String COMMAND_NAME = "togglesprinthunger";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        // Alterna el estado en DifficultyDatabase
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        // Obtiene el estado actual del comando
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);
                        String status = isActive ? "activado" : "desactivado";
                        // Envía el feedback al jugador
                        context.getSource().sendFeedback(
                                () -> Text.literal("Aumento de hambre al correr " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isSprintHungerActive() {
        // Consulta el estado en DifficultyDatabase
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }
}
