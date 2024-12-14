package com.pixelfabric.messages;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.text.MutableText;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CustomMessageCommand {
    // Almacenamiento temporal de mensajes
    private static final Map<String, CustomMessage> messageStorage = new HashMap<>();

    // Enum para los tipos de mensajes
    public enum MessageType {
        MOMENTO_REVIIL("Momento Reviil", Formatting.RED),
        CAMBIO_DIFICULTAD("Cambio de Dificultad", Formatting.GOLD),
        NOTIFICACION("Notificación", Formatting.GREEN),
        MISION_DIARIA("Misión Diaria", Formatting.AQUA),
        DEATH_CICLOPEDIA("DeathCiclopedia", Formatting.YELLOW),
        NOTICIA("Noticia", Formatting.BLUE);

        private final String title;
        private final Formatting color;

        MessageType(String title, Formatting color) {
            this.title = title;
            this.color = color;
        }
    }

    // Clase para almacenar los mensajes
    private static class CustomMessage {
        MessageType type;
        String content;

        CustomMessage(MessageType type) {
            this.type = type;
        }
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Comando para crear un nuevo mensaje
        dispatcher.register(literal("pixelfabricmsg")
                .then(literal("create")
                        .then(argument("messageId", StringArgumentType.word())
                                .then(argument("type", StringArgumentType.word())
                                        .executes(context -> createMessage(
                                                context,
                                                StringArgumentType.getString(context, "messageId"),
                                                StringArgumentType.getString(context, "type")
                                        )))))

                // Comando para establecer el contenido
                .then(literal("setContent")
                        .then(argument("messageId", StringArgumentType.word())
                                .then(argument("content", StringArgumentType.greedyString())
                                        .executes(context -> setContent(
                                                context,
                                                StringArgumentType.getString(context, "messageId"),
                                                StringArgumentType.getString(context, "content")
                                        )))))

                // Comando para enviar el mensaje
                .then(literal("send")
                        .then(argument("messageId", StringArgumentType.word())
                                .then(argument("target", StringArgumentType.word())
                                        .executes(context -> sendMessage(
                                                context,
                                                StringArgumentType.getString(context, "messageId"),
                                                StringArgumentType.getString(context, "target")
                                        )))
                                .executes(context -> sendMessage(
                                        context,
                                        StringArgumentType.getString(context, "messageId"),
                                        "@a"
                                )))));
    }

    private static int createMessage(CommandContext<ServerCommandSource> context, String messageId, String type) {
        try {
            MessageType messageType = getMessageTypeFromString(type);
            messageStorage.put(messageId, new CustomMessage(messageType));

            context.getSource().sendFeedback(() ->
                    Text.literal("Mensaje creado con ID: " + messageId)
                            .formatted(Formatting.GREEN), false);

            return 1;
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(Text.literal("Tipo de mensaje inválido. Tipos válidos: Momento Reviil, Cambio de Dificultad, Notificación, Misión Diaria, DeathCiclopedia, Noticia"));
            return 0;
        }
    }

    private static int setContent(CommandContext<ServerCommandSource> context, String messageId, String content) {
        CustomMessage message = messageStorage.get(messageId);
        if (message == null) {
            context.getSource().sendError(Text.literal("No existe un mensaje con el ID: " + messageId));
            return 0;
        }

        message.content = content;
        context.getSource().sendFeedback(() ->
                Text.literal("Contenido actualizado para el mensaje: " + messageId)
                        .formatted(Formatting.GREEN), false);

        return 1;
    }

    private static int sendMessage(CommandContext<ServerCommandSource> context, String messageId, String target) {
        CustomMessage message = messageStorage.get(messageId);
        if (message == null) {
            context.getSource().sendError(Text.literal("No existe un mensaje con el ID: " + messageId));
            return 0;
        }

        // Crear el mensaje formateado
        MutableText formattedMessage = Text.literal("")
                .append(Text.literal(message.type.title)
                        .formatted(message.type.color, Formatting.BOLD))
                .append(Text.literal("\n-----------------------------------------------------\n")
                        .formatted(message.type.color))
                .append(Text.literal(message.content)
                        .formatted(message.type.color))
                .append(Text.literal("\n-----------------------------------------------------")
                        .formatted(message.type.color));

        // Enviar el mensaje al objetivo especificado
        if (target.equals("@a")) {
            context.getSource().getServer().getPlayerManager()
                    .broadcast(formattedMessage, false);
        } else {
            ServerPlayerEntity player = context.getSource().getServer().getPlayerManager()
                    .getPlayer(target);
            if (player != null) {
                player.sendMessage(formattedMessage);
            } else {
                context.getSource().sendError(Text.literal("Jugador no encontrado: " + target));
                return 0;
            }
        }

        return 1;
    }

    private static MessageType getMessageTypeFromString(String type) {
        return switch (type.toLowerCase().replace(" ", "_")) {
            case "momento_reviil", "reviil" -> MessageType.MOMENTO_REVIIL;
            case "cambio_de_dificultad", "dificultad" -> MessageType.CAMBIO_DIFICULTAD;
            case "notificacion", "notificación" -> MessageType.NOTIFICACION;
            case "mision_diaria", "misión_diaria" -> MessageType.MISION_DIARIA;
            case "deathciclopedia" -> MessageType.DEATH_CICLOPEDIA;
            case "noticia" -> MessageType.NOTICIA;
            default -> throw new IllegalArgumentException("Tipo de mensaje inválido");
        };
    }
}