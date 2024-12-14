package com.pixelfabric.mission;

import com.pixelfabric.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.item.ItemStack;

import java.util.*;

public abstract class AbstractKillMission implements Mission {
    protected final int requiredKills;
    protected final Set<UUID> blueCoinGiven = new HashSet<>();
    private final MissionDatabase database;

    private ItemStack cachedBlueCoin;

    public AbstractKillMission(int requiredKills) {
        this.requiredKills = requiredKills;
        this.database = MissionDatabase.getInstance();
    }

    @Override
    public int getRequiredKills() {
        return requiredKills;
    }

    @Override
    public void incrementKills(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        if (MissionManager.getInstance().hasCompletedMission(playerId)) {
            return;
        }

        int currentKills = database.getKillProgress(playerId) + 1;
        database.saveKillProgress(playerId, currentKills, blueCoinGiven.contains(playerId));

        if (currentKills >= requiredKills && !blueCoinGiven.contains(playerId)) {
            giveBlueCoin(player);
            blueCoinGiven.add(playerId);
            database.saveKillProgress(playerId, currentKills, true);
        } else if (currentKills % 5 == 0) {
            sendProgressMessage(player, currentKills);
        }
    }

    private void sendProgressMessage(ServerPlayerEntity player, int currentKills) {
        player.sendMessage(
                Text.literal("Progreso: " + currentKills + "/" + requiredKills)
                        .formatted(Formatting.YELLOW),
                true
        );
    }

    private void giveBlueCoin(ServerPlayerEntity player) {
        if (cachedBlueCoin == null) {
            cachedBlueCoin = new ItemStack(ModItems.BLUECOIN);
        }

        ItemStack blueCoin = cachedBlueCoin.copy();
        player.getInventory().insertStack(blueCoin);

        player.playSound(
                SoundEvents.ENTITY_PLAYER_LEVELUP,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F
        );
    }

    @Override
    public boolean checkCompletion(PlayerEntity player) {
        UUID playerId = player.getUuid();
        int currentKills = database.getKillProgress(playerId);

        if (currentKills < requiredKills) {
            sendProgressMessage((ServerPlayerEntity)player, currentKills);
            return false;
        }

        ItemStack blueCoin = null;
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty() && stack.isOf(ModItems.BLUECOIN)) {
                blueCoin = stack;
                break;
            }
        }

        if (blueCoin == null) {
            player.sendMessage(
                    Text.literal("Necesitas una Deathcoin azul para reclamar esta recompensa.")
                            .formatted(Formatting.RED),
                    false
            );
            return false;
        }

        blueCoin.decrement(1);
        return true;
    }

    public void reset(UUID playerId) {
        database.saveKillProgress(playerId, 0, false);
        blueCoinGiven.remove(playerId);
    }

    public int getCurrentKills(UUID playerId) {
        return database.getKillProgress(playerId);
    }
}