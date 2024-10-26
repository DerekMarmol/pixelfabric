package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class DoubleMobDamage {
    private static boolean doubleDamageActive = false;

    public static void init() {
        // Registrar el comando para activar/desactivar el daño doble
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggledoubledamage")
                    .requires(source -> source.hasPermissionLevel(2)) // Requiere nivel de permiso de operador
                    .executes(context -> {
                        doubleDamageActive = !doubleDamageActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Daño doble de mobs " + (doubleDamageActive ? "activado" : "desactivado")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static float modifyDamage(LivingEntity target, DamageSource source, float amount) {
        if (doubleDamageActive && target instanceof PlayerEntity && !source.isSourceCreativePlayer()) {
            return amount * 2;
        }
        return amount;
    }

    public static boolean isDoubleDamageActive() {
        return doubleDamageActive;
    }
}