package com.pixelfabric.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class RiskyEnderPearls {
    private static boolean riskyEnderPearlsActive = false;

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toggleriskyenderpearls")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        riskyEnderPearlsActive = !riskyEnderPearlsActive;
                        context.getSource().sendFeedback(
                                () -> Text.literal("Ender pearls arriesgadas " + (riskyEnderPearlsActive ? "activadas" : "desactivadas")),
                                true
                        );
                        return 1;
                    })
            );
        });
    }

    public static float calculateEnderPearlDamage(LivingEntity entity) {
        if (riskyEnderPearlsActive) {
            return entity.getHealth() / 2.0f;
        }
        // Si no está activo, devolvemos el daño por defecto de las ender pearls
        return 5.0f;
    }

    public static boolean isRiskyEnderPearlsActive() {
        return riskyEnderPearlsActive;
    }
}
