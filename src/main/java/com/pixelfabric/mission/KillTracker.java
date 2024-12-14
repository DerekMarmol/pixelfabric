package com.pixelfabric.mission;

import com.pixelfabric.entity.ModEntities;
import com.pixelfabric.mission.missions.KillSkeletonsMission;
import com.pixelfabric.mission.missions.KillSoldierBeeMission;
import com.pixelfabric.mission.missions.KillZombiesMission;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;

public class KillTracker {
    public static void trackKill(ServerPlayerEntity player, EntityType<?> entityType) {
        Mission activeMission = MissionManager.getInstance().getActiveMission();

        if (activeMission == null || MissionManager.getInstance().hasCompletedMission(player.getUuid())) {
            return;
        }

        if (activeMission instanceof AbstractKillMission killMission) {
            if ((activeMission instanceof KillSkeletonsMission && entityType == EntityType.SKELETON) ||
                    (activeMission instanceof KillZombiesMission && entityType == EntityType.ZOMBIE) ||
                    // Agrega esta línea
                    (activeMission instanceof KillSoldierBeeMission && entityType == ModEntities.Soldier_Bee)) {
                killMission.incrementKills(player);
            }
        }
    }
}
