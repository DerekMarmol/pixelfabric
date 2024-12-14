package com.pixelfabric.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.pixelfabric.minigames.PinataMinigame;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PinataCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("pinata")
                .requires(source -> source.hasPermissionLevel(2)) // Requiere nivel de operador
                .then(CommandManager.literal("start")
                        .then(CommandManager.literal("test") // Modo prueba - ignora mínimo de jugadores
                                .executes(context -> {
                                    PinataMinigame.getInstance().startTestMode();
                                    context.getSource().sendFeedback(() ->
                                            Text.literal("§aModo prueba iniciado"), true);
                                    return 1;
                                }))
                        .executes(context -> {
                            PinataMinigame.getInstance().startGame();
                            context.getSource().sendFeedback(() ->
                                    Text.literal("§a¡Minijuego de Piñata iniciado!"), true);
                            return 1;
                        }))
                .then(CommandManager.literal("stop")
                        .executes(context -> {
                            PinataMinigame.getInstance().endGame("El juego ha sido detenido por un administrador.");
                            context.getSource().sendFeedback(() ->
                                    Text.literal("§cMinijuego de Piñata detenido."), true);
                            return 1;
                        }))
                .then(CommandManager.literal("join")
                        .executes(context -> {
                            if (context.getSource().getPlayer() != null) {
                                PinataMinigame.getInstance().addPlayer(context.getSource().getPlayer());
                            }
                            return 1;
                        }))
                .then(CommandManager.literal("config")
                        .then(CommandManager.literal("minPlayers")
                                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 8))
                                        .executes(context -> {
                                            int newMin = IntegerArgumentType.getInteger(context, "count");
                                            PinataMinigame.getInstance().setMinPlayers(newMin);
                                            context.getSource().sendFeedback(() ->
                                                    Text.literal("§aMínimo de jugadores configurado a: " + newMin), true);
                                            return 1;
                                        })))
                        .then(CommandManager.literal("maxPlayers")
                                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 16))
                                        .executes(context -> {
                                            int newMax = IntegerArgumentType.getInteger(context, "count");
                                            PinataMinigame.getInstance().setMaxPlayers(newMax);
                                            context.getSource().sendFeedback(() ->
                                                    Text.literal("§aMáximo de jugadores configurado a: " + newMax), true);
                                            return 1;
                                        })))
                        .then(CommandManager.literal("status")
                                .executes(context -> {
                                    String status = PinataMinigame.getInstance().getGameStatus();
                                    context.getSource().sendFeedback(() ->
                                            Text.literal("§eEstado del juego:\n" + status), false);
                                    return 1;
                                }))));
    }
}