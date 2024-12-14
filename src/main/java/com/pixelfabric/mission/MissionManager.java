package com.pixelfabric.mission;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pixelfabric.mission.missions.*;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MissionManager {
    private static MissionManager instance;
    private Mission activeMission;
    private final Map<String, Mission> availableMissions = new HashMap<>();
    private final Set<UUID> pendingRewards = new HashSet<>();
    private final MissionDatabase database;

    private MissionManager() {
        System.out.println("Inicializando MissionManager...");
        database = MissionDatabase.getInstance();

        registerMission("diamond_armor", new DiamondArmorMission());
        registerMission("iron_armor", new IronArmorMission());
        registerMission("enchanted_diamond_armor", new EnchantedDiamondArmorMission());
        registerMission("kill_zombies", new KillZombiesMission(50));
        registerMission("kill_skeleton", new KillSkeletonsMission(30));
        registerMission("kill_soldier_bee", new KillSoldierBeeMission(25));
        registerMission("craft_backpack", new BackpackCraftingMission());
        registerMission("craft_wither_sword", new WitherSwordCraftingMission());

        String activeMissionId = database.getActiveMission();
        if (activeMissionId != null && availableMissions.containsKey(activeMissionId)) {
            activeMission = availableMissions.get(activeMissionId);
            System.out.println("Misión cargada desde DB: " + activeMissionId);
        }

        System.out.println("Misiones registradas: " + availableMissions.keySet());
    }

    public static MissionManager getInstance() {
        if (instance == null) {
            instance = new MissionManager();
        }
        return instance;
    }

    public void registerMission(String id, Mission mission) {
        availableMissions.put(id, mission);
    }

    public boolean activateMission(String missionId) {
        if (availableMissions.containsKey(missionId)) {
            activeMission = availableMissions.get(missionId);
            database.saveActiveMission(missionId);
            database.clearAllProgress();
            pendingRewards.clear();
            return true;
        }
        return false;
    }

    public boolean hasCompletedMission(UUID playerId) {
        return database.isMissionCompleted(playerId);
    }

    public void addPendingReward(UUID playerId) {
        pendingRewards.add(playerId);
    }

    public boolean hasPendingReward(UUID playerId) {
        return pendingRewards.contains(playerId);
    }

    public void clearPendingReward(UUID playerId) {
        pendingRewards.remove(playerId);
    }

    public boolean tryCompleteMission(ServerPlayerEntity player) {
        System.out.println("Intentando completar misión para: " + player.getName().getString());

        if (activeMission == null) {
            player.sendMessage(Text.literal("No hay misión activa actualmente.")
                    .formatted(Formatting.RED), false);
            return false;
        }

        UUID playerId = player.getUuid();
        if (hasCompletedMission(playerId)) {
            player.sendMessage(Text.literal("Ya has completado la misión diaria.")
                    .formatted(Formatting.YELLOW), false);
            return false;
        }

        if (activeMission.checkCompletion(player)) {
            activeMission.giveReward(player);
            database.setMissionCompleted(playerId, true);

            Text completionMessage = Text.literal("¡" + player.getName().getString() +
                            " ha reclamado su recompensa de la misión diaria!")
                    .formatted(Formatting.GREEN, Formatting.BOLD);

            player.getServer().getPlayerManager().broadcast(completionMessage, false);
            player.playSound(
                    SoundEvents.ENTITY_PLAYER_LEVELUP,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );

            return true;
        } else {
            if (activeMission instanceof AbstractKillMission) {
                int currentKills = database.getKillProgress(playerId);
                int requiredKills = ((AbstractKillMission) activeMission).getRequiredKills();

                player.sendMessage(Text.literal("Progreso actual: " +
                                currentKills + "/" + requiredKills)
                        .formatted(Formatting.RED), false);
            } else {
                player.sendMessage(Text.literal("No has cumplido los requisitos: " +
                                activeMission.getDescription())
                        .formatted(Formatting.RED), false);
            }
            return false;
        }
    }

    public void deactivateMission() {
        database.clearAllProgress();
        activeMission = null;
        database.saveActiveMission(null);
        pendingRewards.clear();
    }

    public Mission getActiveMission() {
        return activeMission;
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("missionactive")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("mission_id", StringArgumentType.string())
                        .executes(context -> {
                            String missionId = StringArgumentType.getString(context, "mission_id");
                            boolean success = getInstance().activateMission(missionId);

                            if (success) {
                                context.getSource().sendFeedback(
                                        () -> Text.literal("Misión activada: " + missionId),
                                        true
                                );
                            } else {
                                context.getSource().sendError(
                                        Text.literal("Misión no encontrada: " + missionId)
                                );
                            }
                            return 1;
                        })));

        dispatcher.register(literal("missiondeactive")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    getInstance().deactivateMission();
                    context.getSource().sendFeedback(
                            () -> Text.literal("Misión desactivada"),
                            true
                    );
                    return 1;
                }));

        dispatcher.register(literal("setmissionprogress")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("player", EntityArgumentType.player())
                        .then(argument("kills", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                    int kills = IntegerArgumentType.getInteger(context, "kills");

                                    Mission activeMission = getInstance().getActiveMission();
                                    if (activeMission instanceof AbstractKillMission) {
                                        getInstance().database.saveKillProgress(
                                                player.getUuid(),
                                                kills,
                                                kills >= ((AbstractKillMission) activeMission).getRequiredKills()
                                        );
                                        context.getSource().sendFeedback(
                                                () -> Text.literal("Progreso establecido a " + kills +
                                                        " para " + player.getName().getString()),
                                                true
                                        );
                                    } else {
                                        context.getSource().sendError(
                                                Text.literal("No hay una misión de matar mobs activa")
                                        );
                                    }

                                    return 1;
                                }))));
    }
}
