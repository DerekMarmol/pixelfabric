package com.pixelfabric;

import com.pixelfabric.Enchantments.ModEnchantments;
import com.pixelfabric.block.ModBlocks;
import com.pixelfabric.callbacks.EntityKillCallback;
import com.pixelfabric.commands.*;
import com.pixelfabric.config.DifficultyDatabase;
import com.pixelfabric.effects.ModEffects;
import com.pixelfabric.entity.*;
import com.pixelfabric.entity.custom.*;
import com.pixelfabric.events.*;
import com.pixelfabric.item.ModItems;
import com.pixelfabric.item.ModItemsGroups;
import com.pixelfabric.messages.CustomMessageCommand;
import com.pixelfabric.minigames.BingoGame;
import com.pixelfabric.minigames.PinataMinigame;
import com.pixelfabric.mission.KillTracker;
import com.pixelfabric.mission.MissionManager;
import com.pixelfabric.network.CreeperFlashPacket;
import com.pixelfabric.network.ModMessages;
import com.pixelfabric.sound.ModSounds;
import com.pixelfabric.timer.TimerCommand;
import com.pixelfabric.updater.ModAutoUpdater;
import com.pixelfabric.utils.ModLootTableModifiers;
import com.pixelfabric.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.pixelfabric.entity.ModEntities.*;

public class PixelFabric implements ModInitializer {
	public static final String MOD_ID = "pixelfabric";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static ModAutoUpdater updater;


	@Override
	public void onInitialize() {
		ModItemsGroups.registerItemGroups();
		ModItems.registerModItems();
		ModEffects.registerEffects();
		EventListeners.register();
		ModEnchantments.registerModEnchantments();
		KillEventHandler.register();
		ModMessages.registerC2SPackets();
		ModWorldGeneration.generateModWorldGen();
		MissionManager.getInstance();
		ModBlocks.registerModBlocks();
		RuletaCommand.register();
		CreeperFlashPacket.register();
		ModEvents.register();
		ModSounds.registerSounds();
		TimerCommand.registerTickEvent();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			NameCommand.register(dispatcher);
		});
		ModLootTableModifiers.init();
		PinataMinigame.initialize(
				new BlockPos(100, 64, 100),  // Arena spawn - ajusta estas coordenadas
				new BlockPos(100, 64, 90)    // Lobby spawn - ajusta estas coordenadas
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CustomMessageCommand.register(dispatcher);
			TimerCommand.register(dispatcher);
			LOGGER.info("Comandos registrados");
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			SetMobCapCommand.register(dispatcher);
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CustomEventSystem.registerCommands(dispatcher);
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				MissionCommands.register(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModSpawnCommands.register(dispatcher);
		});
		CommandRegistrationCallback.EVENT.register(PinataCommands::register);
		EntityKillCallback.EVENT.register((player, entityType) -> {
			KillTracker.trackKill(player, entityType);
		});
		ModAutoUpdater updater = new ModAutoUpdater("1.3.6");
		updater.checkAndUpdate();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			BingoGame.registerCommands(dispatcher);
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			MissionManager.registerCommands(dispatcher);
		});
		// Registrar el evento de tick del servidor
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			// Verificar los objetivos de cada jugador
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				BingoGame.getInstance().checkPlayerActivity(player);
			}
			BingoGame.getInstance().tick();
		});
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			DifficultyDatabase.initDatabase();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			DifficultyDatabase.closeDatabase();
		});
		//Cambios de dificultad
		BetrayalMode.init();
		UnreliableTotem.init();
		NetherDanger.init();
		DoubleMobDamage.init();
		NightmareMode.init();
		EnhancedMobResistance.init();
		SkeletonWitherMechanic.init();
		SkeletonEnhancementMechanic.init();
		RiskyEnderPearls.init();
		EnvironmentalHazards.init();
		RiskyBlazeMechanic.init();
		UnstableShields.init();
		ZombieEnhancementMechanic.init();
		BadFoodMode.init();
		ZombieX6Enhancement.init();
		ZombieX9Enhancement.init();
		PhantomEnhancementMechanic.init();
		PhantomX6Enhancement.init();
		CreeperFlashEnhancement.init();
		SkeletonDamageX3Enhancement.init();
		PhantomInventoryShuffleMechanic.init();

		//cambios dia 4
		SprintHungerMechanic.init();
		SkeletonAccuracyMechanic.init();
		FallDamageMechanic.init();

		//cambios día 6
		TripleMobResistance.init();

		//cambios dia 7
		SpiderDoubleHealthMechanic.init();

		//cambios dia 11
		SpiderAggressionMechanic.init();

		//cambios dia 14
		CreeperExplosionMechanic.init();

		//cambios dia 15
		PersistentFireMechanic.init();

		FabricDefaultAttributeRegistry.register(CHAOS_PHANTOM, ChaosPhantomEntity.createChaosPhantomAttributes());
		FabricDefaultAttributeRegistry.register(BONE_SPIDER, BoneSpiderEntity.createBoneSpiderAttributes());
		FabricDefaultAttributeRegistry.register(ATURTED_PHANTOM, AturdedPhantomEntity.createAturtedPhantomAttributes());
		FabricDefaultAttributeRegistry.register(MINER_ZOMBIE, MinerZombieEntity.createMinerZombieAttributes());
		FabricDefaultAttributeRegistry.register(EXPLODING_SKELETON, ExplodingSkeletonEntity.createAbstractSkeletonAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.LAVASQUID, Lava_SquidEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(CANDIK, CandikEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(PINATA_BURRITO, PinataBurritoEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(MOOBLOOM,
				MoobloomEntity.createCowAttributes()
						.add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0) // Más vida que una vaca normal
						.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2));
		FabricDefaultAttributeRegistry.register(INFERNAL_BULL, InfernalBullEntity.createAttributes());
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