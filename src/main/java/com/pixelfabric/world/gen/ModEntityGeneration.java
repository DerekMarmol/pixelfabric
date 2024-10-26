package com.pixelfabric.world.gen;

import com.pixelfabric.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.Map;

public class ModEntityGeneration {
    public static class SpawnManager {
        private static Map<String, Boolean> mobSpawnStates = new HashMap<>();
        private static Map<String, Boolean> alternativeSpawnStates = new HashMap<>();

        public static void registerMob(String mobId) {
            mobSpawnStates.put(mobId, false);
            alternativeSpawnStates.put(mobId + "_alternative", false);
        }

        public static boolean isSpawnEnabled(String mobId) {
            return mobSpawnStates.getOrDefault(mobId, false);
        }

        public static boolean isAlternativeSpawnEnabled(String mobId) {
            return alternativeSpawnStates.getOrDefault(mobId + "_alternative", false);
        }

        public static void toggleSpawn(String mobId, boolean state) {
            mobSpawnStates.put(mobId, state);
        }

        public static void toggleAlternativeSpawn(String mobId, boolean state) {
            alternativeSpawnStates.put(mobId + "_alternative", state);
        }
    }

    public static void registerSpawnRules() {
        // Reglas del Golem
        SpawnRestriction.register(ModEntities.Golem,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canGolemSpawn);

        // Reglas del Hellhound
        SpawnRestriction.register(ModEntities.Hellhound,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canHellhoundSpawn);

        // Reglas del Lava Spider
        SpawnRestriction.register(ModEntities.LAVA_SPIDER,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ModEntityGeneration::canLavaSpiderSpawn);

        // Registramos todos los mobs
        SpawnManager.registerMob("golem");
        SpawnManager.registerMob("hellhound");
        SpawnManager.registerMob("lava_spider");
    }

    private static boolean canGolemSpawn(EntityType<?> type, ServerWorldAccess world,
                                         SpawnReason spawnReason, BlockPos pos, Random random) {
        boolean normalSpawnEnabled = SpawnManager.isSpawnEnabled("golem");
        boolean alternativeSpawnEnabled = SpawnManager.isAlternativeSpawnEnabled("golem");

        if (!normalSpawnEnabled && !alternativeSpawnEnabled) {
            return false;
        }

        if (world.getDimension().ultrawarm()) {
            return HostileEntity.canSpawnIgnoreLightLevel(
                    (EntityType<? extends HostileEntity>) type, world, spawnReason, pos, random);
        }

        return alternativeSpawnEnabled && HostileEntity.canSpawnIgnoreLightLevel(
                (EntityType<? extends HostileEntity>) type, world, spawnReason, pos, random);
    }

    private static boolean canHellhoundSpawn(EntityType<?> type, ServerWorldAccess world,
                                             SpawnReason spawnReason, BlockPos pos, Random random) {
        boolean normalSpawnEnabled = SpawnManager.isSpawnEnabled("hellhound");
        boolean alternativeSpawnEnabled = SpawnManager.isAlternativeSpawnEnabled("hellhound");

        if (!normalSpawnEnabled && !alternativeSpawnEnabled) {
            return false;
        }

        if (world.getDimension().ultrawarm()) {
            return HostileEntity.canSpawnIgnoreLightLevel(
                    (EntityType<? extends HostileEntity>) type, world, spawnReason, pos, random);
        }

        return alternativeSpawnEnabled && HostileEntity.canSpawnIgnoreLightLevel(
                (EntityType<? extends HostileEntity>) type, world, spawnReason, pos, random);
    }

    private static boolean canLavaSpiderSpawn(EntityType<?> type, ServerWorldAccess world,
                                              SpawnReason spawnReason, BlockPos pos, Random random) {
        boolean normalSpawnEnabled = SpawnManager.isSpawnEnabled("lava_spider");
        boolean alternativeSpawnEnabled = SpawnManager.isAlternativeSpawnEnabled("lava_spider");

        // Si ninguno está activado, no permitir spawn
        if (!normalSpawnEnabled && !alternativeSpawnEnabled) {
            return false;
        }

        // Si es el Nether, permitimos spawn si cualquiera está activado (normal o alternativo)
        if (world.getDimension().ultrawarm()) {
            return HostileEntity.canSpawnIgnoreLightLevel(
                    (EntityType<? extends HostileEntity>) type, world, spawnReason, pos, random);
        }

        // Si es el Overworld, solo permitimos spawn si el spawn alternativo está activado
        if (alternativeSpawnEnabled) {
            // Ignoramos las condiciones de luz para permitir que spawneen también de día
            return HostileEntity.canSpawnIgnoreLightLevel(
                    (EntityType<? extends HostileEntity>) type, world, spawnReason, pos, random);
        }

        // Comportamiento normal para spawn
        return false;
    }


    public static void addSpawns() {
        // Spawn del Golem
        BiomeModifications.addSpawn(
                BiomeSelectors.all(),
                SpawnGroup.MONSTER,
                ModEntities.Golem,
                20,
                1,
                4);

        // Spawn alternativo del Golem (Nether + Overworld)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        BiomeKeys.NETHER_WASTES,
                        BiomeKeys.CRIMSON_FOREST,
                        BiomeKeys.PLAINS,
                        BiomeKeys.DESERT,
                        BiomeKeys.SAVANNA),
                SpawnGroup.MONSTER,
                ModEntities.Golem,
                15,
                1,
                4);

        // Spawn del Hellhound (sólo Nether)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.SOUL_SAND_VALLEY),
                SpawnGroup.MONSTER,
                ModEntities.Hellhound,
                25,
                1,
                3);

        // Spawn alternativo del Hellhound (Nether + Overworld)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        BiomeKeys.SOUL_SAND_VALLEY,
                        BiomeKeys.PLAINS,
                        BiomeKeys.DESERT,
                        BiomeKeys.SAVANNA),
                SpawnGroup.MONSTER,
                ModEntities.Hellhound,
                20,
                1,
                3);

        // Spawn normal de Lava Spider (solo Nether)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        BiomeKeys.NETHER_WASTES,
                        BiomeKeys.CRIMSON_FOREST),
                SpawnGroup.MONSTER,
                ModEntities.LAVA_SPIDER,
                20,
                1,
                1);

        // Spawn alternativo de Lava Spider (Nether + Overworld)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        BiomeKeys.NETHER_WASTES,
                        BiomeKeys.CRIMSON_FOREST,
                        BiomeKeys.PLAINS,
                        BiomeKeys.DESERT,
                        BiomeKeys.SAVANNA),
                SpawnGroup.MONSTER,
                ModEntities.LAVA_SPIDER,
                15,
                1,
                1);
    }
}
