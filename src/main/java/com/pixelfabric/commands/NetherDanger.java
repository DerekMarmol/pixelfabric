package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NetherDanger {
    private static boolean netherDangerActive = false;

    public static void init() {
        // Registrar el comando para activar/desactivar el Nether peligroso
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglenetherdanger")
                    .requires(source -> source.hasPermissionLevel(2)) // Requiere nivel de permiso de operador
                    .executes(context -> {
                        netherDangerActive = !netherDangerActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Nether peligroso " + (netherDangerActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });

        // Registrar el evento para verificar la armadura en el Nether
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (netherDangerActive) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (isInNether(player) && !hasFullDiamondArmor(player)) {
                        player.setOnFireFor(1); // Prende fuego al jugador por 1 segundo
                    }
                }
            }
        });
    }

    private static boolean isInNether(ServerPlayerEntity player) {
        return player.getWorld().getRegistryKey().getValue().equals(new Identifier("minecraft:the_nether"));
    }

    private static boolean hasFullDiamondArmor(ServerPlayerEntity player) {
        for (ItemStack armorPiece : player.getArmorItems()) {
            if (armorPiece.isEmpty() ||
                    (armorPiece.getItem() != Items.DIAMOND_HELMET &&
                            armorPiece.getItem() != Items.DIAMOND_CHESTPLATE &&
                            armorPiece.getItem() != Items.DIAMOND_LEGGINGS &&
                            armorPiece.getItem() != Items.DIAMOND_BOOTS)) {
                return false;
            }
        }
        return true;
    }
}