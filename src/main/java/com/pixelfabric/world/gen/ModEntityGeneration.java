package com.pixelfabric.world.gen;

import com.pixelfabric.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.BiomeKeys;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ModEntityGeneration {
    private static final Map<String, Boolean> normalSpawnDimension = new HashMap<>(); // true for Nether, false for Overworld
    private static final int DEFAULT_WEIGHT = 70;
    private static final int MIN_GROUP_SIZE = 1;
    private static final int MAX_GROUP_SIZE = 1;

    private static final Map<String, Boolean> dimensionCache = new HashMap<>();

    public static class SpawnManager {
        private static final Map<String, SpawnState> spawnStates = new HashMap<>();
        private static final Map<String, Integer> biomeCaps = new HashMap<>();

        private static class SpawnState {
            boolean normal;
            boolean alternative;
            int spawnCap;

            SpawnState() {
                this.normal = false;
                this.alternative = false;
                this.spawnCap = 70; // Default cap
            }
        }

        public static void registerMob(String mobId) {
            spawnStates.put(mobId, new SpawnState());
        }

        public static void setSpawnCap(String mobId, int cap) {
            spawnStates.computeIfPresent(mobId, (k, v) -> {
                v.spawnCap = cap;
                return v;
            });
        }

        public static void setBiomeCap(String biomeId, int cap) {
            biomeCaps.put(biomeId, cap);
        }

        public static boolean isSpawnEnabled(String mobId) {
            SpawnState state = spawnStates.get(mobId);
            return state != null && state.normal;
        }

        public static boolean isAlternativeSpawnEnabled(String mobId) {
            SpawnState state = spawnStates.get(mobId);
            return state != null && state.alternative;
        }

        public static void toggleSpawn(String mobId, boolean state) {
            spawnStates.computeIfPresent(mobId, (k, v) -> {
                v.normal = state;
                return v;
            });
        }

        public static void toggleAlternativeSpawn(String mobId, boolean state) {
            spawnStates.computeIfPresent(mobId, (k, v) -> {
                v.alternative = state;
                return v;
            });
        }

        public static int getSpawnCap(String mobId) {
            SpawnState state = spawnStates.get(mobId);
            return state != null ? state.spawnCap : 70;
        }

        public static int getBiomeCap(String biomeId) {
            return biomeCaps.getOrDefault(biomeId, 70);
        }
    }

    private static void initializeNormalSpawnDimensions() {
        normalSpawnDimension.put("hellhound", true);  // Nether
        normalSpawnDimension.put("candik", false);    // Overworld
        normalSpawnDimension.put("golem", false);     // Overworld
        normalSpawnDimension.put("lava_spider", true); // Nether
        normalSpawnDimension.put("skull", false);     // Overworld
        normalSpawnDimension.put("barnacle", false);  // Overworld (water)
        normalSpawnDimension.put("exploding_skeleton", false); // Overworld
        normalSpawnDimension.put("miner_zombie", false); // Overworld
        normalSpawnDimension.put("abeja_soldado", false);
        normalSpawnDimension.put("wildfire", true);  // Nether
        normalSpawnDimension.put("infernal_bull", true);  // Nether
        normalSpawnDimension.put("zombie_tank", false); // Overworld
    }

    private static boolean canEntitySpawn(
            String mobId,
            EntityType<?> type,
            ServerWorldAccess world,
            SpawnReason spawnReason,
            BlockPos pos,
            Random random
    ) {
        boolean normalSpawnEnabled = SpawnManager.isSpawnEnabled(mobId);
        boolean alternativeSpawnEnabled = SpawnManager.isAlternativeSpawnEnabled(mobId);

        if (!normalSpawnEnabled && !alternativeSpawnEnabled) {
            return false;
        }

        String dimensionId = world.getDimension().toString();
        boolean isNether = dimensionCache.computeIfAbsent(dimensionId,
                k -> world.getDimension().ultrawarm());

        if (alternativeSpawnEnabled) {
            // For alternative spawn, check if it's in overworld and if the surface is accessible
            if (!isNether) {
                // Check if mob can spawn on surface during day (ignore light level completely)
                BlockPos surfacePos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
                if (world.isSkyVisible(surfacePos)) {
                    // Can spawn on surface regardless of light level or time
                    return true;
                }
            }
            // Fallback to normal hostile spawn rules for alternative spawn in other cases
            return HostileEntity.canSpawnIgnoreLightLevel(
                    (EntityType<? extends HostileEntity>) type, world, spawnReason, pos, random);
        }

        if (normalSpawnEnabled) {
            boolean shouldSpawnInNether = normalSpawnDimension.getOrDefault(mobId, false);

            if (isNether != shouldSpawnInNether) {
                return false;
            }

            // For normal spawn, always check darkness
            return HostileEntity.canSpawnInDark(
                    (EntityType<? extends HostileEntity>) type, world, spawnReason, pos, random);
        }

        return false;
    }

    private static boolean canCandikSpawn(EntityType<?> type, ServerWorldAccess world,
                                          SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("candik", type, world, spawnReason, pos, random);
    }

    private static boolean canGolemSpawn(EntityType<?> type, ServerWorldAccess world,
                                         SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("golem", type, world, spawnReason, pos, random);
    }

    private static boolean canBeeSoldierSpawn(EntityType<?> type, ServerWorldAccess world,
                                              SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("abeja_soldado", type, world, spawnReason, pos, random);
    }

    private static boolean canHellhoundSpawn(EntityType<?> type, ServerWorldAccess world,
                                             SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("hellhound", type, world, spawnReason, pos, random);
    }

    private static boolean canLavaSpiderSpawn(EntityType<?> type, ServerWorldAccess world,
                                              SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("lava_spider", type, world, spawnReason, pos, random);
    }

    private static boolean canSkullSpawn(EntityType<?> type, ServerWorldAccess world,
                                         SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("skull", type, world, spawnReason, pos, random);
    }

    private static boolean canExplodingSkeletonSpawn(EntityType<?> type, ServerWorldAccess world,
                                                     SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("exploding_skeleton", type, world, spawnReason, pos, random);
    }

    private static boolean canMinerZombieSpawn(EntityType<?> type, ServerWorldAccess world,
                                               SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("miner_zombie", type, world, spawnReason, pos, random);
    }

    private static boolean canBarnacleSpawn(EntityType<?> type, ServerWorldAccess world,
                                            SpawnReason spawnReason, BlockPos pos, Random random) {
        if (!SpawnManager.isSpawnEnabled("barnacle")) {
            return false;
        }
        return world.getBlockState(pos).isOf(Blocks.WATER) &&
                world.getBlockState(pos.up()).isOf(Blocks.WATER);
    }

    private static boolean canWildfireSpawn(EntityType<?> type, ServerWorldAccess world,
                                            SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("wildfire", type, world, spawnReason, pos, random);
    }

    private static boolean canInfernalBullSpawn(EntityType<?> type, ServerWorldAccess world,
                                                SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("infernal_bull", type, world, spawnReason, pos, random);
    }

    private static boolean canZombieTankSpawn(EntityType<?> type, ServerWorldAccess world,
                                              SpawnReason spawnReason, BlockPos pos, Random random) {
        return canEntitySpawn("zombie_tank", type, world, spawnReason, pos, random);
    }

    public static void registerSpawnRules() {
        SpawnRestriction.register(ModEntities.Golem,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canGolemSpawn);

        SpawnRestriction.register(ModEntities.Hellhound,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canHellhoundSpawn);

        SpawnRestriction.register(ModEntities.LAVA_SPIDER,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canLavaSpiderSpawn);

        SpawnRestriction.register(ModEntities.Skull,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canSkullSpawn);

        SpawnRestriction.register(ModEntities.BARNACLE,
                SpawnRestriction.Location.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canBarnacleSpawn);

        SpawnRestriction.register(ModEntities.CANDIK,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canCandikSpawn);

        SpawnRestriction.register(ModEntities.Soldier_Bee,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canBeeSoldierSpawn);

        SpawnRestriction.register(ModEntities.EXPLODING_SKELETON,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canExplodingSkeletonSpawn);

        SpawnRestriction.register(ModEntities.MINER_ZOMBIE,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canMinerZombieSpawn);

        SpawnRestriction.register(ModEntities.Wildfire,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canWildfireSpawn);

        SpawnRestriction.register(ModEntities.INFERNAL_BULL,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canInfernalBullSpawn);

        SpawnRestriction.register(ModEntities.ZOMBIE_TANK,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canZombieTankSpawn);

        registerMobsWithCaps();
    }

    private static void registerMobsWithCaps() {
        String[] mobs = {
                "golem", "hellhound", "lava_spider", "skull",
                "barnacle", "candik", "exploding_skeleton", "miner_zombie",
                "abeja_soldado", "wildfire", "infernal_bull", "zombie_tank"
        };

        for (String mob : mobs) {
            SpawnManager.registerMob(mob);
            SpawnManager.setSpawnCap(mob, 90);
        }
    }

    public static void addSpawns() {
        initializeNormalSpawnDimensions();

        addMobSpawn(ModEntities.Hellhound, 90, BiomeSelectors.foundInTheNether());
        addMobSpawn(ModEntities.Hellhound, 100, BiomeSelectors.foundInOverworld());

        addMobSpawn(ModEntities.INFERNAL_BULL, 90, BiomeSelectors.foundInTheNether());
        addMobSpawn(ModEntities.INFERNAL_BULL, 90,
                BiomeSelectors.foundInTheNether()
                        .and(context -> !context.getBiomeKey().equals(BiomeKeys.BASALT_DELTAS))
        );

        addMobSpawn(ModEntities.Soldier_Bee, 90, BiomeSelectors.foundInOverworld());
        addMobSpawn(ModEntities.Soldier_Bee, 95, BiomeSelectors.foundInTheNether());
        addMobSpawn(ModEntities.Soldier_Bee, 100, BiomeSelectors.includeByKey(
                BiomeKeys.FLOWER_FOREST
        ));

        addMobSpawn(ModEntities.LAVA_SPIDER, 35, BiomeSelectors.foundInTheNether());
        addMobSpawn(ModEntities.LAVA_SPIDER, 40, BiomeSelectors.foundInOverworld());

        addMobSpawn(ModEntities.CANDIK, 90, BiomeSelectors.foundInTheNether());
        addMobSpawn(ModEntities.CANDIK, 100, BiomeSelectors.foundInOverworld());

        addMobSpawn(ModEntities.Golem, 80, BiomeSelectors.foundInOverworld());
        addMobSpawn(ModEntities.Golem, 70, BiomeSelectors.foundInTheNether());

        // Special configuration for Skull with specific biomes
        addMobSpawn(ModEntities.Skull, 100, BiomeSelectors.foundInOverworld());
        addMobSpawn(ModEntities.Skull, 105, BiomeSelectors.foundInTheNether());
        addMobSpawn(ModEntities.Skull, 100, BiomeSelectors.includeByKey(
                BiomeKeys.DARK_FOREST,
                BiomeKeys.DEEP_DARK,
                BiomeKeys.SOUL_SAND_VALLEY,
                BiomeKeys.WARPED_FOREST,
                BiomeKeys.CRIMSON_FOREST,
                BiomeKeys.PLAINS,
                BiomeKeys.SUNFLOWER_PLAINS
        ));

        // Special configuration for Barnacle in aquatic biomes
        addMobSpawn(ModEntities.BARNACLE, 80, BiomeSelectors.includeByKey(
                BiomeKeys.OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.COLD_OCEAN,
                BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.FROZEN_OCEAN,
                BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.LUKEWARM_OCEAN,
                BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN
        ));

        addMobSpawn(ModEntities.EXPLODING_SKELETON, 90, BiomeSelectors.foundInOverworld());
        addMobSpawn(ModEntities.EXPLODING_SKELETON, 100, BiomeSelectors.foundInTheNether());

        addMobSpawn(ModEntities.MINER_ZOMBIE, 90, BiomeSelectors.foundInOverworld());
        addMobSpawn(ModEntities.MINER_ZOMBIE, 95, BiomeSelectors.foundInTheNether());

        addMobSpawn(ModEntities.Wildfire, 90, BiomeSelectors.foundInTheNether());
        addMobSpawn(ModEntities.Wildfire, 100, BiomeSelectors.foundInOverworld());

        addMobSpawn(ModEntities.ZOMBIE_TANK, 70, BiomeSelectors.foundInOverworld());
        addMobSpawn(ModEntities.ZOMBIE_TANK, 85, BiomeSelectors.foundInTheNether());
    }

    // Helper method to add spawns more cleanly
    private static void addMobSpawn(EntityType<?> entityType, int weight, Predicate<BiomeSelectionContext> selector) {
        BiomeModifications.addSpawn(
                selector,
                SpawnGroup.MONSTER,
                entityType,
                weight,
                MIN_GROUP_SIZE,
                MAX_GROUP_SIZE
        );
    }
}