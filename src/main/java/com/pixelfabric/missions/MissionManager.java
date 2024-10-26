package com.pixelfabric.missions;

import com.pixelfabric.missions.DiamondArmorMission;
import com.pixelfabric.missions.Mission;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import java.util.*;

public class MissionManager {
    private static MissionManager instance;
    private Mission activeMission;
    private final Map<UUID, Boolean> completedMissions = new HashMap<>();
    private final Map<String, Mission> availableMissions = new HashMap<>();

    private MissionManager() {
        System.out.println("Inicializando MissionManager..."); // Debug
        registerMission("diamond_armor", new DiamondArmorMission());
        registerMission("iron_armor", new IronArmorMission());
        registerMission("enchanted_diamond_armor", new EnchantedDiamondArmorMission());
        System.out.println("Misiones registradas: " + availableMissions.keySet()); // Debug
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
            completedMissions.clear(); // Resetear completados al cambiar de misión
            return true;
        }
        return false;
    }

    public boolean hasCompletedMission(UUID playerId) {
        return completedMissions.getOrDefault(playerId, false);
    }

    public boolean tryCompleteMission(ServerPlayerEntity player) {
        System.out.println("Intentando completar misión para: " + player.getName().getString()); // Debug

        if (activeMission == null) {
            System.out.println("No hay misión activa"); // Debug
            player.sendMessage(Text.literal("No hay misión activa actualmente.")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return false;
        }

        System.out.println("Misión activa encontrada: " + activeMission.getDescription()); // Debug

        UUID playerId = player.getUuid();
        if (completedMissions.containsKey(playerId)) {
            System.out.println("Jugador ya completó la misión"); // Debug
            player.sendMessage(Text.literal("Ya has completado la misión diaria.")
                    .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), false);
            return false;
        }

        if (activeMission.checkCompletion(player)) {
            System.out.println("Misión completada exitosamente"); // Debug
            activeMission.giveReward(player);
            completedMissions.put(playerId, true);

            Text completionMessage = Text.literal("¡" + player.getName().getString() + " ha completado la misión diaria!")
                    .setStyle(Style.EMPTY
                            .withColor(Formatting.GREEN)
                            .withBold(true));

            player.getServer().getPlayerManager().broadcast(completionMessage, false);

            player.playSound(
                    SoundEvents.ENTITY_PLAYER_LEVELUP,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );

            return true;
        } else {
            System.out.println("Requisitos de la misión no cumplidos"); // Debug
            player.sendMessage(Text.literal("No has cumplido los requisitos: " + activeMission.getDescription())
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
            return false;
        }
    }

    // Cuando se desactiva una misión, limpiamos el progreso
    public void deactivateMission() {
        activeMission = null;
        completedMissions.clear();
    }

    public Mission getActiveMission() {
        return activeMission;
    }
}