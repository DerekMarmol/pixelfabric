package com.pixelfabric.commands;

import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class TripleMobResistance {
    private static final String COMMAND_NAME = "toggletripleresistance";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2)) // Requiere nivel de permiso de operador
                    .executes(context -> {
                        // Alterna el estado en DifficultyDatabase
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);
                        String status = isActive ? "activada" : "desactivada";
                        // Feedback para el usuario
                        context.getSource().sendFeedback(
                                () -> Text.literal("Resistencia triplicada de mobs " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static float modifyDamage(LivingEntity entity, float amount) {
        if (DifficultyDatabase.isCommandActive(COMMAND_NAME) && !(entity instanceof PlayerEntity)) {
            return amount / 3.0f;
        }
        return amount;
    }

    public static boolean isTripleMobResistanceActive() {
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }
}
