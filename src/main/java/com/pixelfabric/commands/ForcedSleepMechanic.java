package com.pixelfabric.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.pixelfabric.config.DifficultyDatabase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ForcedSleepMechanic {
    private static final String COMMAND_NAME = "toggleforcedsleep";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal(COMMAND_NAME)
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        DifficultyDatabase.toggleCommand(COMMAND_NAME);
                        boolean isActive = DifficultyDatabase.isCommandActive(COMMAND_NAME);
                        String status = isActive ? "activado" : "desactivado";
                        context.getSource().sendFeedback(
                                () -> Text.literal("§5Sueño forzado " + status),
                                true
                        );
                        return 1;
                    })
            );
        });

        // Inicializar comandos de debug
        initDebugCommands();
    }

    public static boolean isForcedSleepActive() {
        return DifficultyDatabase.isCommandActive(COMMAND_NAME);
    }

    // Agregar esto TEMPORALMENTE para testing
    public static void initDebugCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // Comando para inicializar el sistema (establece el tiempo inicial)
            dispatcher.register(CommandManager.literal("initsleep")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        NbtCompound playerData = player.writeNbt(new NbtCompound());
                        long currentTime = player.getWorld().getTimeOfDay();

                        playerData.putInt("nightsWithoutSleep", 0);
                        playerData.putLong("lastSleepTime", currentTime);
                        player.readNbt(playerData);

                        context.getSource().sendFeedback(
                                () -> Text.literal("§aSistema de sueño inicializado\n" +
                                        "§7Tiempo base establecido: " + currentTime),
                                false
                        );
                        return 1;
                    })
            );

            // Comando para simular noches sin dormir
            dispatcher.register(CommandManager.literal("debugsleep")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("nights", IntegerArgumentType.integer(0, 20))
                            .executes(context -> {
                                int nights = IntegerArgumentType.getInteger(context, "nights");
                                ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

                                NbtCompound playerData = player.writeNbt(new NbtCompound());
                                long currentTime = player.getWorld().getTimeOfDay();

                                // Establecer el tiempo de último sueño para simular las noches
                                long simulatedLastSleep = currentTime - (nights * 24000L);

                                playerData.putInt("nightsWithoutSleep", nights);
                                playerData.putLong("lastSleepTime", simulatedLastSleep);
                                player.readNbt(playerData);

                                context.getSource().sendFeedback(
                                        () -> Text.literal("§6Debug: Simuladas " + nights + " noches sin dormir\n" +
                                                "§7Tiempo actual: " + currentTime + "\n" +
                                                "§7Último sueño simulado: " + simulatedLastSleep),
                                        false
                                );
                                return 1;
                            })
                    )
            );

            // Comando para ver el estado actual
            dispatcher.register(CommandManager.literal("checksleep")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        NbtCompound playerData = player.writeNbt(new NbtCompound());

                        int nights = playerData.getInt("nightsWithoutSleep");
                        long lastSleep = playerData.getLong("lastSleepTime");
                        long currentTime = player.getWorld().getTimeOfDay();

                        context.getSource().sendFeedback(
                                () -> Text.literal("§6Estado del sueño:\n" +
                                        "§7Noches sin dormir: " + nights + "\n" +
                                        "§7Último sueño: " + lastSleep + "\n" +
                                        "§7Tiempo actual: " + currentTime),
                                false
                        );
                        return 1;
                    })
            );

            // Comando para resetear el sueño
            dispatcher.register(CommandManager.literal("resetsleep")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        NbtCompound playerData = player.writeNbt(new NbtCompound());

                        playerData.putInt("nightsWithoutSleep", 0);
                        playerData.putLong("lastSleepTime", player.getWorld().getTimeOfDay());
                        player.readNbt(playerData);

                        // Limpiar todos los efectos negativos
                        player.removeStatusEffect(StatusEffects.NAUSEA);
                        player.removeStatusEffect(StatusEffects.WEAKNESS);
                        player.removeStatusEffect(StatusEffects.SLOWNESS);
                        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                        player.removeStatusEffect(StatusEffects.HUNGER);
                        player.removeStatusEffect(StatusEffects.BLINDNESS);

                        context.getSource().sendFeedback(
                                () -> Text.literal("§aEstado de sueño reseteado completamente"),
                                false
                        );
                        return 1;
                    })
            );
        });
    }
}