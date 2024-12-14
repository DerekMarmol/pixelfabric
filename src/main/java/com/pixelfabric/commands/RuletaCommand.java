package com.pixelfabric.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pixelfabric.animation.RuletaAnimationSystem;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class RuletaCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("ruleta")
                    .requires(source -> source.hasPermissionLevel(2)) // Requiere nivel de operador
                    .then(CommandManager.argument("type", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                // Sugerencias de tipos de ruleta
                                builder.suggest("roja");
                                builder.suggest("verde");
                                builder.suggest("amarilla");
                                builder.suggest("naranja");
                                builder.suggest("turqueza");
                                builder.suggest("muerte");
                                return builder.buildFuture();
                            })
                            .executes(context -> {
                                String type = StringArgumentType.getString(context, "type");
                                RuletaAnimationSystem.playAnimation(
                                        type,
                                        context.getSource().getPlayer()
                                );
                                return Command.SINGLE_SUCCESS;
                            }))
            );
        });
    }
}