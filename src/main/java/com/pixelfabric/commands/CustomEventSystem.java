package com.pixelfabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CustomEventSystem {
    private static final Map<String, CustomEvent> events = new HashMap<>();
    private static final Map<String, List<CustomEvent>> gameEventTriggers = new HashMap<>();

    // Método para registrar comandos
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("customevent")
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .executes(context -> createEvent(context.getSource(), StringArgumentType.getString(context, "name")))))
                .then(CommandManager.literal("addAction")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .then(CommandManager.argument("action", StringArgumentType.greedyString())
                                        .executes(context -> addAction(context.getSource(), StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "action"))))))
                .then(CommandManager.literal("trigger")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .executes(context -> triggerEvent(context.getSource(), StringArgumentType.getString(context, "name")))))
                .then(CommandManager.literal("linkToGameEvent")
                        .then(CommandManager.argument("eventName", StringArgumentType.word())
                                .then(CommandManager.argument("gameEvent", StringArgumentType.word())
                                        .executes(context -> linkToGameEvent(context.getSource(), StringArgumentType.getString(context, "eventName"), StringArgumentType.getString(context, "gameEvent"))))))
        );
    }

    // Inicialización del sistema de eventos personalizados
    public static void initialize() {
        // Registra los comandos cuando el servidor se inicializa
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            registerCommands(dispatcher);
        });

        // Eventos adicionales como la muerte del jugador
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
                triggerGameEvent("playerDeath", server, player);
                return true; // Permite la muerte
            });
        });
    }

    private static int createEvent(ServerCommandSource source, String name) {
        events.put(name, new CustomEvent(name));
        source.sendFeedback(() -> Text.literal("Event created: " + name), false);
        return 1;
    }

    private static int addAction(ServerCommandSource source, String name, String actionString) {
        CustomEvent event = events.get(name);
        if (event != null) {
            event.addAction(parseAction(actionString));
            source.sendFeedback(() -> Text.literal("Action added to event: " + name), false);
        } else {
            source.sendError(Text.literal("Event not found: " + name));
        }
        return 1;
    }

    private static int triggerEvent(ServerCommandSource source, String name) {
        CustomEvent event = events.get(name);
        if (event != null) {
            event.execute(source.getServer(), source.getPlayer());
            source.sendFeedback(() -> Text.literal("Event triggered: " + name), false);
        } else {
            source.sendError(Text.literal("Event not found: " + name));
        }
        return 1;
    }

    private static int linkToGameEvent(ServerCommandSource source, String eventName, String gameEvent) {
        CustomEvent event = events.get(eventName);
        if (event != null) {
            gameEventTriggers.computeIfAbsent(gameEvent, k -> new ArrayList<>()).add(event);
            source.sendFeedback(() -> Text.literal("Event '" + eventName + "' linked to game event: " + gameEvent), false);
        } else {
            source.sendError(Text.literal("Event not found: " + eventName));
        }
        return 1;
    }

    private static void triggerGameEvent(String gameEvent, MinecraftServer server, ServerPlayerEntity player) {
        List<CustomEvent> eventsToTrigger = gameEventTriggers.get(gameEvent);
        if (eventsToTrigger != null) {
            for (CustomEvent event : eventsToTrigger) {
                event.execute(server, player);
            }
        }
    }

    private static Action parseAction(String actionString) {
        String[] parts = actionString.split(" ", 2);
        String type = parts[0].toLowerCase();
        String content = parts.length > 1 ? parts[1] : "";

        switch (type) {
            case "animation":
                return new AnimationAction(content);
            case "playsound":
                return new PlaySoundAction(content);
            case "delay":
                return new DelayAction(Integer.parseInt(content));
            case "message":
                return new MessageAction(content);
            case "command":
                return new CommandAction(content);
            case "kick":
                return new KickPlayerAction(content);
            default:
                throw new IllegalArgumentException("Unknown action type: " + type);
        }
    }

    private static class CustomEvent {
        private final String name;
        private final List<Action> actions = new ArrayList<>();

        public CustomEvent(String name) {
            this.name = name;
        }

        public void addAction(Action action) {
            actions.add(action);
        }

        public void execute(MinecraftServer server, ServerPlayerEntity player) {
            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
            for (Action action : actions) {
                future = future.thenCompose(v -> action.execute(server, player));
            }
        }
    }

    private interface Action {
        CompletableFuture<Void> execute(MinecraftServer server, ServerPlayerEntity player);
    }

    private static class AnimationAction implements Action {
        private final String animationCommand;

        public AnimationAction(String animationCommand) {
            this.animationCommand = animationCommand;
        }

        @Override
        public CompletableFuture<Void> execute(MinecraftServer server, ServerPlayerEntity player) {
            server.getCommandManager().executeWithPrefix(player.getCommandSource(), "animation " + animationCommand);
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class PlaySoundAction implements Action {
        private final String soundCommand;

        public PlaySoundAction(String soundCommand) {
            this.soundCommand = soundCommand;
        }

        @Override
        public CompletableFuture<Void> execute(MinecraftServer server, ServerPlayerEntity player) {
            server.getCommandManager().executeWithPrefix(player.getCommandSource(), "playsound " + soundCommand);
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class DelayAction implements Action {
        private final int ticks;

        public DelayAction(int ticks) {
            this.ticks = ticks;
        }

        @Override
        public CompletableFuture<Void> execute(MinecraftServer server, ServerPlayerEntity player) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            server.getWorlds().iterator().next().getServer().execute(() -> {
                try {
                    Thread.sleep(ticks * 50);  // 1 tick = 50 ms
                    future.complete(null);
                } catch (InterruptedException e) {
                    future.completeExceptionally(e);
                }
            });
            return future;
        }
    }

    private static class KickPlayerAction implements Action {
        private final String reason;

        public KickPlayerAction(String reason) {
            this.reason = reason;
        }

        @Override
        public CompletableFuture<Void> execute(MinecraftServer server, ServerPlayerEntity player) {
            player.networkHandler.disconnect(Text.literal(reason));
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class MessageAction implements Action {
        private final String message;

        public MessageAction(String message) {
            this.message = message;
        }

        @Override
        public CompletableFuture<Void> execute(MinecraftServer server, ServerPlayerEntity player) {
            player.sendMessage(Text.literal(message));
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class CommandAction implements Action {
        private final String command;

        public CommandAction(String command) {
            this.command = command;
        }

        @Override
        public CompletableFuture<Void> execute(MinecraftServer server, ServerPlayerEntity player) {
            server.getCommandManager().executeWithPrefix(player.getCommandSource(), command);
            return CompletableFuture.completedFuture(null);
        }
    }
}
