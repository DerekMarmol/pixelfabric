package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class SpiderWebMechanic {
    private static boolean spiderWebActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglespiderweb")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        spiderWebActive = !spiderWebActive;
                        String status = spiderWebActive ? "activada" : "desactivada";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Telarañas por arañas " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isSpiderWebActive() {
        return spiderWebActive;
    }
}