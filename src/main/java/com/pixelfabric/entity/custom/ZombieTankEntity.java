package com.pixelfabric.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ZombieTankEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int spawnCooldown = 0;
    private boolean hasSpawnedSquad = false;

    public ZombieTankEntity(EntityType<? extends ZombieTankEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.0F); // Puede subir bloques más altos
        this.experiencePoints = 20; // Más experiencia por ser un mini-boss
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 0.8D, false) {
            @Override
            protected double getSquaredMaxAttackDistance(LivingEntity entity) {
                return 8.0; // Alcance de ataque mayor
            }

            @Override
            protected void attack(LivingEntity target, double squaredDistance) {
                if (squaredDistance <= this.getSquaredMaxAttackDistance(target)) {
                    this.resetCooldown();
                    this.mob.swingHand(net.minecraft.util.Hand.MAIN_HAND); // Añadido esto
                    tryAttack(target);
                }
            }
        });

        this.goalSelector.add(3, new WanderAroundGoal(this, 0.5)); // Movimiento lento
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25) // Más lento que zombies normales pero no tanto
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0) // 30 corazones
                .add(EntityAttributes.GENERIC_ARMOR, 8.0) // Resistencia alta
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 4.0) // Resistencia adicional
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0) // Ataque fuerte
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 3.0) // Knockback alto
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8); // Muy resistente al knockback
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // Reducir daño de flechas y proyectiles
        if (source.isIndirect()) {
            amount *= 0.3f; // Las flechas hacen solo 30% del daño normal
        }

        // Spawnar escuadra si no se ha hecho y tiene poca vida
        if (!hasSpawnedSquad && this.getHealth() < this.getMaxHealth() * 0.5f) {
            spawnProtectiveSquad();
            hasSpawnedSquad = true;
        }

        return super.damage(source, amount);
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (!super.tryAttack(target)) {
            return false;
        }

        // Empujar fuertemente al jugador
        if (target instanceof PlayerEntity player) {
            double xDiff = player.getX() - this.getX();
            double zDiff = player.getZ() - this.getZ();
            double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

            if (distance > 0) {
                xDiff /= distance;
                zDiff /= distance;
            }

            double upwardForce = 0.6;
            double horizontalForce = 3.0; // Empuje muy fuerte

            player.setVelocity(
                    xDiff * horizontalForce,
                    upwardForce,
                    zDiff * horizontalForce
            );

            player.velocityModified = true;
            this.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0F, 0.8F);
        }

        return true;
    }

    private void spawnProtectiveSquad() {
        if (this.getWorld().isClient) return;

        for (int i = 0; i < 3; i++) {
            ZombieEntity zombie = EntityType.ZOMBIE.create(this.getWorld());
            if (zombie != null) {
                // Posicionar zombies alrededor del tanque
                double angle = (i * 2 * Math.PI) / 3; // Distribuir en círculo
                double radius = 2.0;
                double x = this.getX() + Math.cos(angle) * radius;
                double z = this.getZ() + Math.sin(angle) * radius;

                zombie.setPosition(x, this.getY(), z);
                zombie.setTarget(this.getTarget()); // Mismo objetivo que el tanque
                this.getWorld().spawnEntity(zombie);
            }
        }

        // Sonido intimidante al spawnar la escuadra
        this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0F, 0.5F);
    }

    @Override
    public void tick() {
        super.tick();

        if (spawnCooldown > 0) {
            spawnCooldown--;
        }
    }

    // Animaciones
    private PlayState movementPredicate(AnimationState event) {
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            event.setAndContinue(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.5F; // Sonidos más fuertes
    }

}