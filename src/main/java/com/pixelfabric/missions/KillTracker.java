package com.pixelfabric.missions;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillTracker {
    private static final Map<UUID, Integer> playerKills = new HashMap<>();

    public static void trackKill(ServerPlayerEntity player, EntityType<?> entityType) {
        Mission activeMission = MissionManager.getInstance().getActiveMission();

        // Si no hay misión activa o el jugador ya tiene la medalla, no hacemos nada
        if (activeMission == null || hasCompletionToken(player)) {
            return;
        }

        if (activeMission instanceof KillZombiesMission && entityType == EntityType.ZOMBIE) {
            trackKillProgress(player, "zombie_kills", "Zombies", "Trofeo de Cazador de Zombies");
        }
        else if (activeMission instanceof KillSkeletonsMission && entityType == EntityType.SKELETON) {
            trackKillProgress(player, "skeleton_kills", "Esqueletos", "Trofeo de Cazador de Esqueletos");
        }
    }

    private static boolean hasCompletionToken(ServerPlayerEntity player) {
        Mission activeMission = MissionManager.getInstance().getActiveMission();
        String tokenType = null;

        if (activeMission instanceof KillZombiesMission) {
            tokenType = "zombie_kills";
        } else if (activeMission instanceof KillSkeletonsMission) {
            tokenType = "skeleton_kills";
        }

        if (tokenType == null) {
            return false;
        }

        // Revisar si el jugador ya tiene la medalla en su inventario
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                NbtCompound nbt = stack.getNbt();
                if (nbt != null && tokenType.equals(nbt.getString("MissionToken"))) {
                    return true;
                }
            }
        }

        // También revisar si ya completó la misión
        UUID playerId = player.getUuid();
        return MissionManager.getInstance().hasCompletedMission(playerId);
    }

    private static void trackKillProgress(ServerPlayerEntity player, String tokenType, String mobName, String trophyName) {
        UUID playerId = player.getUuid();
        int currentKills = playerKills.getOrDefault(playerId, 0) + 1;
        playerKills.put(playerId, currentKills);

        // Notificar al jugador de su progreso
        player.sendMessage(Text.literal(mobName + " eliminados: " + currentKills + "/10")
                .formatted(Formatting.GREEN), true);

        // Si completa el objetivo, dale el trofeo
        if (currentKills >= 10) {
            giveCompletionToken(player, tokenType, trophyName);
            playerKills.remove(playerId);
        }
    }

    private static void giveCompletionToken(ServerPlayerEntity player, String tokenType, String trophyName) {
        ItemStack token = new ItemStack(Items.PAPER);
        NbtCompound nbt = token.getOrCreateNbt();
        nbt.putString("MissionToken", tokenType);
        token.setCustomName(Text.literal(trophyName)
                .formatted(Formatting.GOLD));

        player.getInventory().insertStack(token);
        player.sendMessage(Text.literal("¡Has recibido un " + trophyName + "! Úsalo en el bloque de misiones para reclamar tu recompensa.")
                .formatted(Formatting.GREEN), false);
    }

    public static void resetProgress(UUID playerId) {
        playerKills.remove(playerId);
    }
}