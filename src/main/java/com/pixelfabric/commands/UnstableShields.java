package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class UnstableShields {
    private static boolean unstableShieldsActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggleunstableshields")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        unstableShieldsActive = !unstableShieldsActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Unstable Shields " + (unstableShieldsActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isUnstableShieldsActive() {
        return unstableShieldsActive;
    }

    public static boolean shouldShieldBreak() {
        return unstableShieldsActive && Math.random() < 0.15;
    }
}
