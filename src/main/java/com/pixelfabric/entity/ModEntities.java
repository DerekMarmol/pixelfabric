package com.pixelfabric.entity;

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
                    dimensions(EntityDimensions.fixed(1.5f,1.75f)).build());

    public static final EntityType<GolemEntity> Golem = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "golem"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GolemEntity::new).
                    dimensions(EntityDimensions.fixed(2.0f, 4.0f)).build());

    public static final EntityType<WraithEntity> Wraith = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "wraith"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WraithEntity::new).
                    dimensions(EntityDimensions.fixed(2.0f, 1.f)).build());

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


}
