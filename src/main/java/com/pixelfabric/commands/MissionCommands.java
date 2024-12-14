package com.pixelfabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pixelfabric.mission.MissionManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MissionCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("missionactive")
                .requires(source -> source.hasPermissionLevel(2)) // Nivel 2 = Operador
                .then(argument("mission_id", StringArgumentType.string())
                        .executes(context -> {
                            String missionId = StringArgumentType.getString(context, "mission_id");
                            boolean success = MissionManager.getInstance().activateMission(missionId);

                            if (success) {
                                context.getSource().sendFeedback(
                                        () -> Text.literal("Misión activada: " + missionId),
                                        true
                                );
                            } else {
                                context.getSource().sendError(Text.literal("Misión no encontrada: " + missionId));
                            }

                            return 1;
                        })));

        dispatcher.register(literal("missiondeactive")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    MissionManager.getInstance().deactivateMission();
                    context.getSource().sendFeedback(
                            () -> Text.literal("Misión desactivada"),
                            true
                    );
                    return 1;
                }));
    }
}