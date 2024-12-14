package com.pixelfabric.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.pixelfabric.config.MobCapConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class SetMobCapCommand {
    private static final Text INVALID_VALUE = Text.literal("El multiplicador debe estar entre 0.5 y 2.0");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        registerSetCommand(dispatcher);
        registerGetCommand(dispatcher);
        registerResetCommand(dispatcher);
    }

    private static void registerSetCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("setmobcap")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("multiplier", FloatArgumentType.floatArg())
                        .executes(context -> executeSet(
                                context.getSource(),
                                FloatArgumentType.getFloat(context, "multiplier")
                        ))
                )
        );
    }

    private static void registerGetCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("getmobcap")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> executeGet(context.getSource()))
        );
    }

    private static void registerResetCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("resetmobcap")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> executeReset(context.getSource()))
        );
    }

    private static int executeSet(ServerCommandSource source, float multiplier) {
        if (MobCapConfig.setMobCapMultiplier(multiplier)) {
            source.sendFeedback(
                    () -> Text.literal("MobCap multiplicador establecido a: " + multiplier + "x"),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }
        source.sendError(INVALID_VALUE);
        return 0;
    }

    private static int executeGet(ServerCommandSource source) {
        float current = MobCapConfig.getMobCapMultiplier();
        source.sendFeedback(
                () -> Text.literal("MobCap multiplicador actual: " + current + "x"),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeReset(ServerCommandSource source) {
        MobCapConfig.reset();
        source.sendFeedback(
                () -> Text.literal("MobCap multiplicador restablecido a valores predeterminados"),
                true
        );
        return Command.SINGLE_SUCCESS;
    }
}
