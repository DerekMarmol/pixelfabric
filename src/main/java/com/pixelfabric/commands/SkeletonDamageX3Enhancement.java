package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class SkeletonDamageX3Enhancement {
    private static boolean damageX3Active = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggleskeletondamagex3")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        damageX3Active = !damageX3Active;
                        String status = damageX3Active ? "activado" : "desactivado";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Cambio de configuración 2/5: Los esqueletos ahora infligen triple daño. Estado del cambio: " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isDamageX3Active() {
        return damageX3Active;
    }
}