package com.pixelfabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.pixelfabric.entity.ModEntities;
import com.pixelfabric.world.gen.ModEntityGeneration;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

public class ModSpawnCommands {
    // List of all available mob IDs
    private static final List<String> MOB_IDS = Arrays.asList(
            "golem", "hellhound", "lava_spider", "skull",
            "barnacle", "candik", "exploding_skeleton", "miner_zombie",
            "abeja_soldado", "wildfire", "infernal_bull", "zombie_tank"
    );

    // Tab completion for mob IDs
    private static final SuggestionProvider<ServerCommandSource> MOB_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(MOB_IDS, builder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        // ===== STATUS COMMAND =====
        dispatcher.register(
                CommandManager.literal("spawnstatus")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            context.getSource().sendFeedback(
                                    () -> Text.literal("=== SPAWN STATUS ===").formatted(Formatting.GOLD),
                                    false
                            );

                            for (String mobId : MOB_IDS) {
                                boolean normalActive = ModEntityGeneration.SpawnManager.isSpawnEnabled(mobId);
                                boolean altActive = ModEntityGeneration.SpawnManager.isAlternativeSpawnEnabled(mobId);
                                int spawnCap = ModEntityGeneration.SpawnManager.getSpawnCap(mobId);

                                String status = getStatusText(normalActive, altActive);
                                Formatting color = getStatusColor(normalActive, altActive);

                                context.getSource().sendFeedback(
                                        () -> Text.literal(String.format("• %s: %s (Cap: %d)",
                                                mobId, status, spawnCap)).formatted(color),
                                        false
                                );
                            }
                            return 1;
                        })
        );

        // ===== COMMANDS WITH TAB COMPLETION =====

        // Activate normal spawn
        dispatcher.register(
                CommandManager.literal("activespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .suggests(MOB_SUGGESTIONS)
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");

                                    if (!MOB_IDS.contains(mobId)) {
                                        context.getSource().sendError(
                                                Text.literal("❌ Unknown mob: " + mobId)
                                        );
                                        return 0;
                                    }

                                    ModEntityGeneration.SpawnManager.toggleSpawn(mobId, true);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("✅ Normal spawn for " + mobId + " activated!")
                                                    .formatted(Formatting.GREEN),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // Activate alternative spawn
        dispatcher.register(
                CommandManager.literal("activealternativespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .suggests(MOB_SUGGESTIONS)
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");

                                    if (!MOB_IDS.contains(mobId)) {
                                        context.getSource().sendError(
                                                Text.literal("❌ Unknown mob: " + mobId)
                                        );
                                        return 0;
                                    }

                                    ModEntityGeneration.SpawnManager.toggleAlternativeSpawn(mobId, true);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("⚡ Alternative spawn for " + mobId + " activated!")
                                                    .formatted(Formatting.YELLOW),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // Deactivate normal spawn
        dispatcher.register(
                CommandManager.literal("deactivespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .suggests(MOB_SUGGESTIONS)
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");

                                    if (!MOB_IDS.contains(mobId)) {
                                        context.getSource().sendError(
                                                Text.literal("❌ Unknown mob: " + mobId)
                                        );
                                        return 0;
                                    }

                                    ModEntityGeneration.SpawnManager.toggleSpawn(mobId, false);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("❌ Normal spawn for " + mobId + " deactivated!")
                                                    .formatted(Formatting.RED),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // Deactivate alternative spawn
        dispatcher.register(
                CommandManager.literal("deactivealternativespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .suggests(MOB_SUGGESTIONS)
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");

                                    if (!MOB_IDS.contains(mobId)) {
                                        context.getSource().sendError(
                                                Text.literal("❌ Unknown mob: " + mobId)
                                        );
                                        return 0;
                                    }

                                    ModEntityGeneration.SpawnManager.toggleAlternativeSpawn(mobId, false);
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("❌ Alternative spawn for " + mobId + " deactivated!")
                                                    .formatted(Formatting.RED),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // ===== BONUS COMMAND: UPGRADE SPAWN =====
        dispatcher.register(
                CommandManager.literal("upgradespawn")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .suggests(MOB_SUGGESTIONS)
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");

                                    if (!MOB_IDS.contains(mobId)) {
                                        context.getSource().sendError(
                                                Text.literal("❌ Unknown mob: " + mobId)
                                        );
                                        return 0;
                                    }

                                    ModEntityGeneration.SpawnManager.toggleSpawn(mobId, false);
                                    ModEntityGeneration.SpawnManager.toggleAlternativeSpawn(mobId, true);

                                    context.getSource().sendFeedback(
                                            () -> Text.literal("🔥 " + mobId + " upgraded to alternative spawn!")
                                                    .formatted(Formatting.GOLD),
                                            true
                                    );
                                    return 1;
                                })
                        )
        );

        // ===== COMMAND TO COUNT ENTITIES =====
        dispatcher.register(
                CommandManager.literal("countmob")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("entity", StringArgumentType.word())
                                .suggests(MOB_SUGGESTIONS)
                                .executes(context -> {
                                    String mobId = StringArgumentType.getString(context, "entity");

                                    if (!MOB_IDS.contains(mobId)) {
                                        context.getSource().sendError(
                                                Text.literal("❌ Unknown mob: " + mobId)
                                        );
                                        return 0;
                                    }

                                    // Get the corresponding EntityType
                                    EntityType<?> entityType = getEntityTypeFromId(mobId);
                                    if (entityType == null) {
                                        context.getSource().sendError(
                                                Text.literal("❌ Could not find entity type for: " + mobId)
                                        );
                                        return 0;
                                    }

                                    // Count entities in current world
                                    ServerWorld world = context.getSource().getWorld();
                                    int count = 0;

                                    for (Entity entity : world.iterateEntities()) {
                                        if (entity.getType() == entityType) {
                                            count++;
                                        }
                                    }

                                    // Final variables for use in lambda
                                    final String finalMobId = mobId;
                                    final int finalCount = count;
                                    final String worldName = world.getRegistryKey().getValue().getPath();

                                    context.getSource().sendFeedback(
                                            () -> Text.literal(String.format("📊 %s: %d entities in %s",
                                                            finalMobId, finalCount, worldName))
                                                    .formatted(Formatting.AQUA),
                                            false
                                    );

                                    return count;
                                })
                        )
        );
    }

    // Helper methods for status command
    private static String getStatusText(boolean normal, boolean alternative) {
        if (normal && alternative) return "Normal + Alternative";
        if (normal) return "Normal";
        if (alternative) return "Alternative";
        return "Disabled";
    }

    private static Formatting getStatusColor(boolean normal, boolean alternative) {
        if (normal && alternative) return Formatting.GOLD;
        if (normal) return Formatting.GREEN;
        if (alternative) return Formatting.YELLOW;
        return Formatting.GRAY;
    }

    // Helper method to map IDs to EntityTypes
    private static EntityType<?> getEntityTypeFromId(String mobId) {
        return switch (mobId) {
            case "golem" -> ModEntities.Golem;
            case "hellhound" -> ModEntities.Hellhound;
            case "lava_spider" -> ModEntities.LAVA_SPIDER;
            case "skull" -> ModEntities.Skull;
            case "barnacle" -> ModEntities.BARNACLE;
            case "candik" -> ModEntities.CANDIK;
            case "exploding_skeleton" -> ModEntities.EXPLODING_SKELETON;
            case "miner_zombie" -> ModEntities.MINER_ZOMBIE;
            case "abeja_soldado" -> ModEntities.Soldier_Bee;
            case "wildfire" -> ModEntities.Wildfire;
            case "infernal_bull" -> ModEntities.INFERNAL_BULL;
            case "zombie_tank" -> ModEntities.ZOMBIE_TANK;
            default -> null;
        };
    }
}