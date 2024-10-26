package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class EnvironmentalHazards {
    private static boolean environmentalHazardsActive = false;

    public static void init() {
        // Registro del comando para activar/desactivar los efectos ambientales
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggleenvironmentalhazards")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        environmentalHazardsActive = !environmentalHazardsActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Efectos ambientales " + (environmentalHazardsActive ? "activados" : "desactivados")),
                                true
                        );
                        return 1;
                    })
            );
        });

        // Registro del evento de tick del servidor
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (environmentalHazardsActive) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    BlockPos pos = player.getBlockPos();
                    String biome = player.getWorld().getBiome(pos).getKey().get().getValue().toString();

                    switch (biome) {
                        case "minecraft:desert":
                            applyDesertEffects(player);
                            break;
                        case "minecraft:swamp":
                            applySwampEffects(player);
                            break;
                        // Puedes añadir más biomas y sus efectos aquí
                    }
                }
            }
        });
    }

    private static void applyDesertEffects(ServerPlayerEntity player) {
        // Ejemplo: aplicar un efecto de sed (ralentización y debilidad)
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1));
    }

    private static void applySwampEffects(ServerPlayerEntity player) {
        // Ejemplo: aplicar un efecto de envenenamiento
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 1));
    }
}
