package com.pixelfabric.events;

import com.pixelfabric.animation.RuletaAnimationSystem;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.Vec2f;

import java.util.*;

public class CustomEventSystem {
    static final Map<String, CustomEvent> events = new HashMap<>();

    private static class EventAction {
        String command;
        boolean isDelay;
        int delaySeconds;

        EventAction(String command) {
            this.command = command;
            this.isDelay = false;
        }

        EventAction(int delaySeconds) {
            this.delaySeconds = delaySeconds;
            this.isDelay = true;
        }
    }

    static class CustomEvent {
        String name;
        String type;
        List<EventAction> actions = new ArrayList<>();

        CustomEvent(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("pixelfabricevent")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .then(CommandManager.argument("type", StringArgumentType.word())
                                        .executes(context -> createEvent(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "name"),
                                                StringArgumentType.getString(context, "type")
                                        )))))
                .then(CommandManager.literal("addaction")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .then(CommandManager.argument("command", StringArgumentType.greedyString())
                                        .executes(context -> addAction(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "name"),
                                                StringArgumentType.getString(context, "command")
                                        )))))
                .then(CommandManager.literal("adddelay")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .then(CommandManager.argument("seconds", IntegerArgumentType.integer(0))
                                        .executes(context -> addDelay(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "name"),
                                                IntegerArgumentType.getInteger(context, "seconds")
                                        )))))
                .then(CommandManager.literal("execute")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(context -> executeEventCommand(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "name"),
                                                EntityArgumentType.getPlayer(context, "target")
                                        )))
                                .executes(context -> executeEventCommand(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "name"),
                                        context.getSource().getPlayer()
                                ))))
                .then(CommandManager.literal("list")
                        .executes(context -> listEvents(context.getSource())))
                .then(CommandManager.literal("delete")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .executes(context -> deleteEvent(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "name")
                                )))));
    }

    public static void triggerCustomEvent(String eventName, ServerPlayerEntity player) {
        CustomEvent event = events.get(eventName);
        if (event != null && event.type.equalsIgnoreCase("CUSTOM")) {
            executeEvent(eventName, player);
        }
    }



    private static int executeEventCommand(ServerCommandSource source, String eventName, ServerPlayerEntity target) {
        CustomEvent event = events.get(eventName);
        if (event == null) {
            source.sendFeedback(() -> Text.literal("§cEvento no encontrado: " + eventName), false);
            return 0;
        }

        if (!event.type.equalsIgnoreCase("CUSTOM")) {
            source.sendFeedback(() -> Text.literal("§cSolo se pueden ejecutar manualmente los eventos de tipo CUSTOM"), false);
            return 0;
        }

        executeEvent(eventName, target);
        source.sendFeedback(() -> Text.literal("§aEjecutando evento '" + eventName + "' para " + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int createEvent(ServerCommandSource source, String name, String type) {
        if (events.containsKey(name)) {
            source.sendFeedback(() -> Text.literal("§cYa existe un evento con ese nombre"), false);
            return 0;
        }

        if (!isValidEventType(type)) {
            source.sendFeedback(() -> Text.literal("§cTipo de evento no válido. Tipos válidos: DEATH, TOTEM, JOIN, CUSTOM"), false);
            return 0;
        }

        events.put(name, new CustomEvent(name, type));
        source.sendFeedback(() -> Text.literal("§aEvento '" + name + "' creado con tipo " + type), true);
        return Command.SINGLE_SUCCESS;
    }

    private static boolean isValidEventType(String type) {
        return type.equalsIgnoreCase("DEATH") ||
                type.equalsIgnoreCase("TOTEM") ||
                type.equalsIgnoreCase("JOIN") ||
                type.equalsIgnoreCase("CUSTOM");
    }

    private static int addAction(ServerCommandSource source, String name, String command) {
        CustomEvent event = events.get(name);
        if (event == null) {
            source.sendFeedback(() -> Text.literal("§cEvento no encontrado"), false);
            return 0;
        }

        event.actions.add(new EventAction(command));
        source.sendFeedback(() -> Text.literal("§aComando añadido al evento '" + name + "'"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int addDelay(ServerCommandSource source, String name, int seconds) {
        CustomEvent event = events.get(name);
        if (event == null) {
            source.sendFeedback(() -> Text.literal("§cEvento no encontrado"), false);
            return 0;
        }

        event.actions.add(new EventAction(seconds));
        source.sendFeedback(() -> Text.literal("§aDelay de " + seconds + " segundos añadido al evento '" + name + "'"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int listEvents(ServerCommandSource source) {
        if (events.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§eNo hay eventos configurados"), false);
            return Command.SINGLE_SUCCESS;
        }

        source.sendFeedback(() -> Text.literal("§6Lista de eventos:"), false);
        for (CustomEvent event : events.values()) {
            source.sendFeedback(() -> Text.literal("§e- " + event.name + " (Tipo: " + event.type + ")"), false);
            int index = 1;
            for (EventAction action : event.actions) {
                if (action.isDelay) {
                    int finalIndex1 = index;
                    source.sendFeedback(() -> Text.literal("  §7" + finalIndex1 + ". Delay: " + action.delaySeconds + "s"), false);
                } else {
                    int finalIndex = index;
                    source.sendFeedback(() -> Text.literal("  §7" + finalIndex + ". " + action.command), false);
                }
                index++;
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int deleteEvent(ServerCommandSource source, String name) {
        if (!events.containsKey(name)) {
            source.sendFeedback(() -> Text.literal("§cEvento no encontrado"), false);
            return 0;
        }

        events.remove(name);
        source.sendFeedback(() -> Text.literal("§aEvento '" + name + "' eliminado"), true);
        return Command.SINGLE_SUCCESS;
    }

    static void executeEvent(String eventName, ServerPlayerEntity player) {
        CustomEvent event = events.get(eventName);
        if (event == null) return;

        executeActionsSequentially(event, player, 0, 0);
    }

    private static void executeActionsSequentially(CustomEvent event, ServerPlayerEntity player, int actionIndex, int totalDelay) {
        if (actionIndex >= event.actions.size()) return;

        EventAction action = event.actions.get(actionIndex);

        if (action.isDelay) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    executeActionsSequentially(event, player, actionIndex + 1, totalDelay + action.delaySeconds);
                }
            }, action.delaySeconds * 1000L);
        } else {
            String command = action.command
                    .replace("%player%", player.getName().getString())
                    .replace("%x%", String.valueOf(player.getX()))
                    .replace("%y%", String.valueOf(player.getY()))
                    .replace("%z%", String.valueOf(player.getZ()));

            // Nuevo método para ejecutar comandos
            executeCommandWithContext(player.getServer(), player, command);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    executeActionsSequentially(event, player, actionIndex + 1, totalDelay);
                }
            }, 50);
        }
    }

    private static void executeCommandWithContext(MinecraftServer server, ServerPlayerEntity player, String command) {
        // Eliminar el '/' inicial si existe
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        // Verificar si es un comando de ruleta primero
        if (command.startsWith("ruleta ")) {
            String[] parts = command.split(" ");
            if (parts.length == 2) {
                String type = parts[1];
                RuletaAnimationSystem.playAnimation(type, player);
                return; // Salir después de ejecutar la animación
            }
        }

        // Si no es un comando de ruleta, ejecutar normalmente
        try {
            ServerCommandSource source = server.getCommandSource()
                    .withWorld((ServerWorld)player.getWorld())
                    .withPosition(player.getPos())
                    .withRotation(new Vec2f(player.getYaw(), player.getPitch()))
                    .withEntity(player);

            // Asegurarse de que el comando no tenga '/' al inicio
            server.getCommandManager().executeWithPrefix(source, command);
        } catch (Exception e) {
            // Log del error
            System.err.println("Error executing command: " + command);
            e.printStackTrace();
        }
    }
}