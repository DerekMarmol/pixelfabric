package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ZombieEnhancementMechanic {
    private static boolean zombieEnhancementActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglezombieenhancement")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        zombieEnhancementActive = !zombieEnhancementActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Mejora de zombies " + (zombieEnhancementActive ? "activada" : "desactivada")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isZombieEnhancementActive() {
        return zombieEnhancementActive;
    }
}
