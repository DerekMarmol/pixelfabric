package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class RiskyBlazeMechanic {
    private static boolean riskyBlazeSwapActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggleriskyblazeswap")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        riskyBlazeSwapActive = !riskyBlazeSwapActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Intercambio de posición con Blaze " + (riskyBlazeSwapActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isRiskyBlazeSwapActive() {
        return riskyBlazeSwapActive;
    }

    public static void swapPositions(Entity entity1, Entity entity2) {
        Vec3d pos1 = entity1.getPos();
        Vec3d pos2 = entity2.getPos();

        entity1.teleport(pos2.x, pos2.y, pos2.z);
        entity2.teleport(pos1.x, pos1.y, pos1.z);

    }
}