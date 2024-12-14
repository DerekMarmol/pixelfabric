package com.pixelfabric.entity.custom;

import com.pixelfabric.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.ArrayList;
import java.util.List;

public class GeneratorEntity extends Entity implements GeoAnimatable {
    private static final int MAX_HEALTH = 100;
    private static final int SPAWN_RADIUS = 10;
    private static final int PLAYER_DETECTION_RANGE = 16;
    private static final int MIN_SPAWN_COOLDOWN = 400; // 20 ticks = 1 segundo
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private int spawnCooldown = 0;
    private List<Entity> spawnedEntities = new ArrayList<>();
    private boolean isSpawningHorde = false;

    public GeneratorEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setInvulnerable(false);
    }

    @Override
    protected void initDataTracker() {
        // Inicializar datos que necesitemos trackear
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isClient) {
            // Verificar jugadores cercanos
            Box box = new Box(getX() - PLAYER_DETECTION_RANGE, getY() - PLAYER_DETECTION_RANGE, getZ() - PLAYER_DETECTION_RANGE,
                    getX() + PLAYER_DETECTION_RANGE, getY() + PLAYER_DETECTION_RANGE, getZ() + PLAYER_DETECTION_RANGE);

            List<PlayerEntity> nearbyPlayers = getWorld().getEntitiesByClass(
                    PlayerEntity.class,
                    box,
                    player -> player.squaredDistanceTo(this) < PLAYER_DETECTION_RANGE * PLAYER_DETECTION_RANGE
            );

            if (!nearbyPlayers.isEmpty() && spawnCooldown <= 0) {
                // Verificar si quedan pocos mobs de la horda anterior
                if (spawnedEntities.removeIf(Entity::isRemoved) && spawnedEntities.size() <= 1) {
                    spawnHorde(nearbyPlayers.size());
                }
            }

            if (spawnCooldown > 0) {
                spawnCooldown--;
            }
        }
    }

    private void spawnHorde(int playerCount) {
        isSpawningHorde = true;
        int baseSpawnCount = 3 + playerCount; // Más mobs por jugador

        // Distribuir tipos de mobs
        for (int i = 0; i < baseSpawnCount; i++) {
            double chance = getWorld().getRandom().nextDouble();
            Entity spawnedEntity;

            if (chance < 0.4) {
                spawnedEntity = ModEntities.Wraith.create(getWorld());
            } else if (chance < 0.7) {
                spawnedEntity = ModEntities.CANDLE_SWORD.create(getWorld());
            } else {
                spawnedEntity = ModEntities.CANDIK.create(getWorld());
            }

            if (spawnedEntity != null) {
                // Posición aleatoria dentro del radio
                double angle = getWorld().getRandom().nextDouble() * 2 * Math.PI;
                double radius = getWorld().getRandom().nextDouble() * SPAWN_RADIUS;
                double x = this.getX() + Math.cos(angle) * radius;
                double z = this.getZ() + Math.sin(angle) * radius;
                double y = this.getY();

                spawnedEntity.setPosition(x, y, z);
                getWorld().spawnEntity(spawnedEntity);
                spawnedEntities.add(spawnedEntity);
            }
        }

        // Activar animación de hit y reiniciar cooldown
        triggerHitAnimation();
        spawnCooldown = MIN_SPAWN_COOLDOWN + (playerCount * 100); // Más cooldown con más jugadores
        isSpawningHorde = false;
    }

    private void triggerHitAnimation() {
        // Aquí implementarías la lógica para activar la animación
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(
                this,
                "controller",
                0,
                event -> {
                    if (!isAlive()) {
                        event.setAnimation(RawAnimation.begin().thenPlay("animation.generator.fall"));
                        return PlayState.CONTINUE;
                    }

                    if (isSpawningHorde) {
                        event.setAnimation(RawAnimation.begin().thenPlay("animation.generator.hit"));
                        return PlayState.CONTINUE;
                    }

                    return PlayState.STOP;
                }
        ));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return age;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        spawnCooldown = nbt.getInt("SpawnCooldown");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("SpawnCooldown", spawnCooldown);
    }
}