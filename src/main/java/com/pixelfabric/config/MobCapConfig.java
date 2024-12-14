package com.pixelfabric.config;

public final class MobCapConfig {
    private static final float DEFAULT_MULTIPLIER = 1.0f;
    private static final float MIN_MULTIPLIER = 0.5f;
    private static final float MAX_MULTIPLIER = 2.0f;

    private static float mobCapMultiplier = DEFAULT_MULTIPLIER;

    private MobCapConfig() {} // Previene instanciación

    public static float getMobCapMultiplier() {
        return mobCapMultiplier;
    }

    public static boolean setMobCapMultiplier(float multiplier) {
        if (multiplier >= MIN_MULTIPLIER && multiplier <= MAX_MULTIPLIER) {
            mobCapMultiplier = multiplier;
            return true;
        }
        return false;
    }

    public static void reset() {
        mobCapMultiplier = DEFAULT_MULTIPLIER;
    }
}