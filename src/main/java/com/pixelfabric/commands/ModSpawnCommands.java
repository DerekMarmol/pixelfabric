package com.pixelfabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pixelfabric.world.gen.ModEntityGeneration;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ModSpawnCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Comando para spawn normal
        dispatcher.register(
                CommandManager.literal("activespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");
                                    ModEntityGeneration.SpawnManager.toggleSpawn(mobId, true);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("¡Spawn normal de " + mobId + " activado!"),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // Comando para spawn alternativo
        dispatcher.register(
                CommandManager.literal("activealternativespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");
                                    ModEntityGeneration.SpawnManager.toggleAlternativeSpawn(mobId, true);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("¡Spawn alternativo de " + mobId + " activado!"),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // Comando para desactivar spawn normal
        dispatcher.register(
                CommandManager.literal("deactivespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");
                                    ModEntityGeneration.SpawnManager.toggleSpawn(mobId, false);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("¡Spawn normal de " + mobId + " desactivado!"),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // Comando para desactivar spawn alternativo
        dispatcher.register(
                CommandManager.literal("deactivealternativespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");
                                    ModEntityGeneration.SpawnManager.toggleAlternativeSpawn(mobId, false);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("¡Spawn alternativo de " + mobId + " desactivado!"),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );
    }
}