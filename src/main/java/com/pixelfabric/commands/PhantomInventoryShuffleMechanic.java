package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class PhantomInventoryShuffleMechanic {
    private static boolean inventoryShuffleActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglephantomshuffle")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        inventoryShuffleActive = !inventoryShuffleActive;
                        String status = inventoryShuffleActive ? "activada" : "desactivada";
                        context.getSource().sendFeedback(
                                () -> Text.literal("Revolver inventario por phantoms " + status),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isInventoryShuffleActive() {
        return inventoryShuffleActive;
    }
}