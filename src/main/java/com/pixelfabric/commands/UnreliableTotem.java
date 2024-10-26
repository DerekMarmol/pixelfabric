package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.util.Random;

public class UnreliableTotem {
    private static boolean unreliableTotemActive = false;
    private static final Random random = new Random();

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggleunreliabletotem")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        unreliableTotemActive = !unreliableTotemActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Tótems poco fiables " + (unreliableTotemActive ? "activados" : "desactivados")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean shouldTotemWork() {
        if (!unreliableTotemActive) {
            return true;
        }
        return random.nextBoolean();
    }
}
