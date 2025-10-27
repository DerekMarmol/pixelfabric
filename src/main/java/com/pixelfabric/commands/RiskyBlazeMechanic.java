package com.pixelfabric.commands;

import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class RiskyBlazeMechanic {
    private static final String COMMAND_NAME = "riskyblazeswap";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggle" + COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        // Cambiar el estado en la database
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);

                        context.getSource().sendFeedback(
                                () -> Text.literal("Intercambio de posición con Blaze " + (isActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static boolean isRiskyBlazeSwapActive() {
        // Leer directamente el estado de la database
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }

    public static void swapPositions(Entity entity1, Entity entity2) {
        Vec3d pos1 = entity1.getPos();
        Vec3d pos2 = entity2.getPos();

        entity1.teleport(pos2.x, pos2.y, pos2.z);
        entity2.teleport(pos1.x, pos1.y, pos1.z);
    }
}
