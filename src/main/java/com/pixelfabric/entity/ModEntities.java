package com.pixelfabric.entity;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import static com.pixelfabric.PixelFabric.MOD_ID;

public class ModEntities {
    public static final EntityType<Soldier_BeeEntity> Soldier_Bee = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID,"abeja_soldado"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Soldier_BeeEntity::new).
                    dimensions(EntityDimensions.fixed(1.0f,1.70f)).build());

    public static final EntityType<GolemEntity> Golem = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "golem"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GolemEntity::new).
                    dimensions(EntityDimensions.fixed(1.8f, 3.8f)).build());

    public static final EntityType<WraithEntity> Wraith = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "wraith"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WraithEntity::new).
                    dimensions(EntityDimensions.fixed(2.0f, 1.8f)).build());

    public static final EntityType<WildfireEntity> Wildfire = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "wildfire"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WildfireEntity::new).
                    dimensions(EntityDimensions.fixed(2.0f, 2.0f)).build());

    public static final EntityType<Octana_ExplodeEntity> LAVA_SPIDER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "lava_spider"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, Octana_ExplodeEntity::new)
                    .dimensions(EntityDimensions.fixed(2.0f, 3f))  // Ajuste de dimensiones de la araña
                    .build());

    public static final EntityType<Candle_SwordEntity> CANDLE_SWORD = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "candle_sword"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, Candle_SwordEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1.5f))
                    .build());

    public static final EntityType<HellhoundEntity> Hellhound = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "hellhound"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, HellhoundEntity::new)
                    .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
                    .build());

    public static final EntityType<PumpkinFiendeEntity> Pumpkin = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "pumpkin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, PumpkinFiendeEntity::new)
                    .dimensions(EntityDimensions.fixed(2.5f, 2.5f))
                    .build());

    public static final EntityType<OwlbearEntity> Owlbear = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "owlbear"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, OwlbearEntity::new)
                    .dimensions(EntityDimensions.fixed(4.5f, 4.0f))
                    .build());

    public static final EntityType<AltarEntity> Altar = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "altar"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, AltarEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 2.0f))
                    .trackRangeBlocks(32)
                    .forceTrackedVelocityUpdates(false)
                    .trackedUpdateRate(1)
                    .build());

    public static final EntityType<SkullEntity> Skull = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "skull"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkullEntity::new)
                    .dimensions(EntityDimensions.fixed(2.0f, 2.0f))
                    .build());

    public static final EntityType<BarnacleEntity> BARNACLE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "barnacle"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BarnacleEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1.2f))
                    .build());

    public static final EntityType<CandikEntity> CANDIK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "candik"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, CandikEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build());

    public static final EntityType<Lava_SquidEntity> LAVASQUID = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "lavaquid"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, Lava_SquidEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 1.5f))
                    .build());

    public static final EntityType<ExplodingSkeletonEntity> EXPLODING_SKELETON = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "exploding_skeleton"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ExplodingSkeletonEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6F, 1.99F))
                    .build()
    );

    public static final EntityType<ExplosiveArrowEntity> EXPLOSIVE_ARROW = Registry.register(
            Registries.ENTITY_TYPE, new Identifier("tu_mod_id", "explosive_arrow"),
            FabricEntityTypeBuilder.<ExplosiveArrowEntity>create(SpawnGroup.MISC, ExplosiveArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .trackRangeBlocks(4).trackedUpdateRate(20)
                    .build()
    );

    public static final EntityType<MinerZombieEntity> MINER_ZOMBIE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "miner_zombie"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MinerZombieEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
                    .build()
    );

    public static final EntityType<BoneSpiderEntity> BONE_SPIDER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("pixelfabric", "bone_spider"),
            EntityType.Builder.create(BoneSpiderEntity::new, SpawnGroup.MONSTER)
                    .setDimensions(1.4F, 0.9F)
                    .build("bone_spider")
    );

    public static final EntityType<ChaosPhantomEntity> CHAOS_PHANTOM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("pixelfabric", "chaos_phantom"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ChaosPhantomEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9F, 0.9F))
                    .build()
    );

    public static final EntityType<AturdedPhantomEntity> ATURTED_PHANTOM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("pixelfabric", "aturded_phantom"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AturdedPhantomEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9F, 0.9F))
                    .build()
    );

    public static final EntityType<MoobloomEntity> MOOBLOOM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "moobloom"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MoobloomEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 1.4f)) // Mismas dimensiones que la vaca
                    .trackRangeBlocks(10)
                    .build()
    );

    public static final EntityType<GeneratorEntity> GENERATOR_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("pixelfabric", "generator"),
            FabricEntityTypeBuilder.create()
                    .entityFactory(GeneratorEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 1.5f)) // Tamaño de la entidad (ancho, alto)
                    .build()
    );

    public static final EntityType<InfernalBullEntity> INFERNAL_BULL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("pixelfabric", "infernal_bull"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, InfernalBullEntity::new)
                    .dimensions(EntityDimensions.fixed(2.0f, 2.0f))
                    .build()
    );

    public static final EntityType<PinataBurritoEntity> PINATA_BURRITO = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("pixelfabric", "pinata_burrito"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, PinataBurritoEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 2.0f)) // Ajusta estas dimensiones según el tamaño de tu modelo
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(1)
                    .forceTrackedVelocityUpdates(true)
                    .build()
    );

    public static final EntityType<ZombieTankEntity> ZOMBIE_TANK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "zombie_tank"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombieTankEntity::new)
                    .dimensions(EntityDimensions.fixed(2.0F, 2.9F)) // Más alto y ancho que zombies normales
                    .build()
    );

    public static final EntityType<AngryChickenEntity> ANGRY_CHICKEN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("pixelfabric", "angry_chicken"),
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(AngryChickenEntity::new)
                    .spawnGroup(SpawnGroup.CREATURE)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.7f)) // igual que el pollo
                    .build()
    );

    public static final EntityType<ToxinSpiderEntity> TOXIN_SPIDER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "toxin_spider"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ToxinSpiderEntity::new)
                    .dimensions(EntityDimensions.fixed(3f, 2f)) // 3 veces más grande que araña vanilla (1.4x0.9)
                    .build()
    );

    public static final EntityType<ToxinProjectileEntity> TOXIN_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "toxin_projectile"),
            FabricEntityTypeBuilder.<ToxinProjectileEntity>create(SpawnGroup.MISC, ToxinProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .trackRangeBlocks(10)
                    .trackedUpdateRate(10)
                    .build()
    );


}
