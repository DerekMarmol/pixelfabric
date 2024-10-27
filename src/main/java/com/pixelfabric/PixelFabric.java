package com.pixelfabric;

import com.pixelfabric.callbacks.EntityKillCallback;
import com.pixelfabric.commands.*;
import com.pixelfabric.entity.ModEntities;
import com.pixelfabric.entity.custom.*;
import com.pixelfabric.events.KillEventHandler;
import com.pixelfabric.item.ModItems;
import com.pixelfabric.item.ModItemsGroups;
import com.pixelfabric.missions.KillSkeletonsMission;
import com.pixelfabric.missions.KillTracker;
import com.pixelfabric.missions.KillZombiesMission;
import com.pixelfabric.missions.MissionManager;
import com.pixelfabric.network.ModMessages;
import com.pixelfabric.world.gen.ModWorldGeneration;
import com.updater.ModAutoUpdater;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PixelFabric implements ModInitializer {
	public static final String MOD_ID = "pixelfabric";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		ModItemsGroups.registerItemGroups();
		ModItems.registerModItems();

		MissionManager.getInstance(); // Esto registrará las misiones por defecto

		// Registrar los comandos
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				MissionCommands.register(dispatcher));

		NetherDanger.init();
		DoubleMobDamage.init();
		MissionManager.getInstance().registerMission("kill_skeletons", new KillSkeletonsMission());
		MissionManager.getInstance().registerMission("kill_zombies", new KillZombiesMission());
		NightmareMode.init();
		BetrayalMode.init();
		UnreliableTotem.init();
		EnhancedMobResistance.init();
		RiskyEnderPearls.init();
		EnvironmentalHazards.init();
		RiskyBlazeMechanic.init();
		ModMessages.registerC2SPackets();
		ModWorldGeneration.generateModWorldGen();
		CustomEventSystem.initialize();
		UnstableShields.init();
		ZombieEnhancementMechanic.init();
		ModAutoUpdater updater = new ModAutoUpdater();
		updater.checkForUpdates();
		KillEventHandler.register();
		BadFoodMode.init();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModSpawnCommands.register(dispatcher);
		});

		EntityKillCallback.EVENT.register((player, entityType) -> {
			KillTracker.trackKill(player, entityType);
		});

		FabricDefaultAttributeRegistry.register(ModEntities.BARNACLE, BarnacleEntity.createDevourerAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.Skull, SkullEntity.createSkullAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.Owlbear, OwlbearEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.Hellhound, HellhoundEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.Soldier_Bee, Soldier_BeeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.Golem, GolemEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.Wraith, WraithEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.Pumpkin, PumpkinFiendeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CANDLE_SWORD, Candle_SwordEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.LAVA_SPIDER, Octana_ExplodeEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.Wildfire, WildfireEntity.createWildfireAttributes());
	}
}