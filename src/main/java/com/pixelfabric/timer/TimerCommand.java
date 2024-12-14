package com.pixelfabric.timer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class TimerCommand {
    private static int remainingTicks = 0;
    private static boolean isTimerActive = false;
    private static boolean isMinutes = false;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("pixelfabrictimer")
                .then(argument("type", StringArgumentType.word())
                        .then(argument("time", IntegerArgumentType.integer(1))
                                .executes(context -> startTimer(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "type"),
                                        IntegerArgumentType.getInteger(context, "time")
                                )))));
    }

    private static int startTimer(ServerCommandSource source, String type, int time) {
        if (isTimerActive) {
            source.sendError(Text.literal("Ya hay un temporizador activo"));
            return 0;
        }

        isMinutes = type.equalsIgnoreCase("minutos");

        // Convertir a ticks (20 ticks = 1 segundo)
        remainingTicks = time * 20; // Para segundos
        if (isMinutes) {
            remainingTicks *= 60; // Convertir minutos a segundos y luego a ticks
        }

        isTimerActive = true;

        // Notificar a todos los jugadores que el timer ha comenzado
        String timeType = isMinutes ? "minutos" : "segundos";
        source.getServer().getPlayerManager().broadcast(
                Text.literal("¡Temporizador iniciado! " + time + " " + timeType)
                        .formatted(Formatting.GREEN),
                false
        );

        return 1;
    }

    public static void registerTickEvent() {
        ServerTickEvents.END_SERVER_TICK.register(server -> tick(server));
    }

    private static void tick(MinecraftServer server) {
        if (!isTimerActive) return;

        if (remainingTicks > 0) {
            remainingTicks--;

            // Actualizar el action bar cada segundo (cada 20 ticks)
            if (remainingTicks % 20 == 0) {
                updateDisplay(server);
            }
        } else {
            // Timer terminado
            isTimerActive = false;
            server.getPlayerManager().broadcast(
                    Text.literal("¡Tiempo terminado!")
                            .formatted(Formatting.RED, Formatting.BOLD),
                    false
            );
        }
    }

    private static void updateDisplay(MinecraftServer server) {
        int secondsRemaining = remainingTicks / 20;
        String display;

        if (isMinutes) {
            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;
            display = String.format("⏰ %02d:%02d", minutes, seconds);
        } else {
            display = String.format("⏰ %d segundos", secondsRemaining);
        }

        // Determinar el color basado en el tiempo restante
        Formatting color = Formatting.GREEN;
        if (secondsRemaining <= 10) {
            color = Formatting.RED;
        } else if (secondsRemaining <= 30) {
            color = Formatting.YELLOW;
        }

        // Crear el texto del action bar
        Text timerText = Text.literal(display).formatted(color);

        // Mostrar a todos los jugadores usando el action bar
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(timerText, true); // El true indica que es un action bar message
        }
    }
}