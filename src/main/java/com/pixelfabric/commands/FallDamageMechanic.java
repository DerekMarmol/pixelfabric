package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class FallDamageMechanic {
    private static boolean fallDamageModifierActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglefalldamage")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        fallDamageModifierActive = !fallDamageModifierActive;
                        String status = fallDamageModifierActive ? "activado" : "desactivado";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Aumento del daño por caída " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isFallDamageModifierActive() {
        return fallDamageModifierActive;
    }
}