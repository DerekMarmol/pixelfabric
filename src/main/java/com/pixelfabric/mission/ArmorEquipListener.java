package com.pixelfabric.mission;

import com.pixelfabric.mission.missions.DiamondArmorMission;
import com.pixelfabric.mission.missions.EnchantedDiamondArmorMission;
import com.pixelfabric.mission.missions.IronArmorMission;
import net.minecraft.server.network.ServerPlayerEntity;

public class ArmorEquipListener {
    public static void onArmorEquip(ServerPlayerEntity player) {
        Mission activeMission = MissionManager.getInstance().getActiveMission();
        MissionManager missionManager = MissionManager.getInstance();

        if (!missionManager.hasCompletedMission(player.getUuid())) {
            if (activeMission instanceof EnchantedDiamondArmorMission enchantedMission) {
                if (enchantedMission.checkArmorPieces(player)) {
                    missionManager.addPendingReward(player.getUuid());
                    enchantedMission.checkAndReward(player);
                }
            } else if (activeMission instanceof DiamondArmorMission diamondMission) {
                if (diamondMission.checkArmorPieces(player)) {
                    missionManager.addPendingReward(player.getUuid());
                    diamondMission.checkAndReward(player);
                }
            } else if (activeMission instanceof IronArmorMission ironArmorMission){
                if (ironArmorMission.checkArmorPieces(player)){
                    missionManager.addPendingReward(player.getUuid());
                    ironArmorMission.checkAndReward(player);
                }
            }
        }
    }
}