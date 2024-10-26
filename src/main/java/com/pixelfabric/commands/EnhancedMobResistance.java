package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class EnhancedMobResistance {
    private static boolean enhancedResistanceActive = false;
    private static final float RESISTANCE_MULTIPLIER = 0.85f; // 15% de reducción de daño

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("togglemobresistance")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        enhancedResistanceActive = !enhancedResistanceActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Resistencia mejorada de mobs " + (enhancedResistanceActive ? "activada" : "desactivada")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static float modifyIncomingDamage(LivingEntity entity, float amount) {
        if (enhancedResistanceActive && !(entity instanceof PlayerEntity)) {
            return amount * RESISTANCE_MULTIPLIER;
        }
        return amount;
    }

    public static boolean isEnhancedResistanceActive() {
        return enhancedResistanceActive;
    }
}
