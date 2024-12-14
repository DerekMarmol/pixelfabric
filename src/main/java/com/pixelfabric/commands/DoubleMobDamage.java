package com.pixelfabric.commands;

import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class DoubleMobDamage {
    private static final String COMMAND_NAME = "toggledoubledamage";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2)) // Requiere nivel de permiso de operador
                    .executes(context -> {
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);
                        String status = isActive ? "activado" : "desactivado";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Daño doble de mobs " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static float modifyDamage(LivingEntity target, DamageSource source, float amount) {
       if (DifficultyDatabase.isCommandActive(COMMAND_NAME) && target instanceof PlayerEntity && !source.isSourceCreativePlayer()) {
            return amount * 2;
        }
        return amount;
    }

    public static boolean isDoubleDamageActive() {
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }
}
