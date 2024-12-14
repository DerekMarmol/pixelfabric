package com.pixelfabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NameCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("pxname")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("ocultar")
                        .then(argument("target", EntityArgumentType.player())
                                .executes(context -> hidePlayerName(context))))
                .then(literal("change")
                        .then(argument("target", EntityArgumentType.player())
                                .then(argument("newName", StringArgumentType.string())
                                        .executes(context -> changePlayerName(context))))));
    }

    private static int hidePlayerName(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "target");

            // Ocultar nombre completamente
            targetPlayer.setCustomName(Text.literal(""));
            targetPlayer.setCustomNameVisible(false);

            // Actualizar lista de jugadores para todos los clientes
            updatePlayerList(targetPlayer);

            context.getSource().sendFeedback(
                    () -> Text.literal("Nombre del jugador ocultado"),
                    true
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error al ocultar el nombre: " + e.getMessage()));
            return 0;
        }
    }

    private static int changePlayerName(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "target");
            String newName = StringArgumentType.getString(context, "newName");

            // Cambiar nombre de visualización
            targetPlayer.setCustomName(Text.literal(newName));
            targetPlayer.setCustomNameVisible(true);

            // Actualizar lista de jugadores para todos los clientes
            updatePlayerList(targetPlayer);

            context.getSource().sendFeedback(
                    () -> Text.literal("Nombre del jugador cambiado a: " + newName),
                    true
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error al cambiar el nombre: " + e.getMessage()));
            return 0;
        }
    }

    private static void updatePlayerList(ServerPlayerEntity player) {
        // Enviar paquete de actualización de lista de jugadores a todos los jugadores conectados
        PlayerListS2CPacket packet = new PlayerListS2CPacket(
                PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME,
                player
        );

        player.getServer().getPlayerManager().sendToAll(packet);
    }
}