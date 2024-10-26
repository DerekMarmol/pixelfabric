package com.pixelfabric.world.gen;

public class ModWorldGeneration {
    public static void generateModWorldGen() {
        ModEntityGeneration.registerSpawnRules();
        ModEntityGeneration.addSpawns();
    }
}