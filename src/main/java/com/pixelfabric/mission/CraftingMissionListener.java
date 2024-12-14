package com.pixelfabric.mission;

import net.minecraft.server.network.ServerPlayerEntity;

public class CraftingMissionListener {
    public static void checkInventory(ServerPlayerEntity player) {
        Mission activeMission = MissionManager.getInstance().getActiveMission();
        MissionManager missionManager = MissionManager.getInstance();

        if (!missionManager.hasCompletedMission(player.getUuid())) {
            if (activeMission instanceof AbstractCraftingMission craftingMission) {
                craftingMission.checkAndReward(player);
            }
        }
    }
}