package com.pixelfabric.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class Soldier_BeeEntity extends HostileEntity implements GeoEntity {
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private int attackCooldown = 0;

    public Soldier_BeeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.navigation = new BirdNavigation(this, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.4f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SoldierBeeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(3, new FlyRandomlyGoal(this));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.75f));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
    }

    private PlayState predicate(AnimationState<Soldier_BeeEntity> event) {
        if (this.isOnGround()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.soldier_bee.idle", Animation.LoopType.LOOP));
        } else {
            return event.setAndContinue(RawAnimation.begin().then("animation.soldier_bee.walk", Animation.LoopType.LOOP));
        }
    }

    private PlayState attackPredicate(AnimationState<Soldier_BeeEntity> event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            event.setAndContinue(RawAnimation.begin().then("animation.soldier_bee.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, this::predicate));
        data.add(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (!this.isOnGround() && this.getVelocity().y < 0) {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }
    }

    private void applyPoisonEffect(LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 1));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 40, 0));

        if (this.getWorld() instanceof ServerWorld) {
            ((ServerWorld) this.getWorld()).spawnParticles(
                    ParticleTypes.ENTITY_EFFECT,
                    target.getX(),
                    target.getY() + target.getHeight() / 2,
                    target.getZ(),
                    10,
                    0.5,
                    0.5,
                    0.5,
                    0.1
            );
        }
    }

    private static class FlyRandomlyGoal extends Goal {
        private final Soldier_BeeEntity bee;
        private Vec3d targetPos;

        public FlyRandomlyGoal(Soldier_BeeEntity bee) {
            this.bee = bee;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !bee.hasPassengers();
        }

        @Override
        public void start() {
            this.targetPos = this.getRandomLocation();
        }

        @Override
        public void tick() {
            if (this.targetPos == null || this.bee.squaredDistanceTo(targetPos.x, targetPos.y, targetPos.z) < 2.0) {
                this.targetPos = this.getRandomLocation();
            }

            if (this.targetPos != null) {
                this.bee.getMoveControl().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.0);
            }
        }

        private Vec3d getRandomLocation() {
            Random random = bee.getRandom();
            double x = bee.getX() + (random.nextDouble() * 2 - 1) * 8;
            double y = bee.getY() + (random.nextDouble() * 2 - 1) * 4;
            double z = bee.getZ() + (random.nextDouble() * 2 - 1) * 8;
            return new Vec3d(x, y, z);
        }
    }

    private class SoldierBeeAttackGoal extends MeleeAttackGoal {
        public SoldierBeeAttackGoal(Soldier_BeeEntity bee, double speed, boolean pauseWhenMobIdle) {
            super(bee, speed, pauseWhenMobIdle);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && Soldier_BeeEntity.this.attackCooldown == 0;
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.getCooldown() <= 0) {
                this.resetCooldown();
                this.mob.swingHand(Hand.MAIN_HAND);
                if (this.mob.tryAttack(target)) {
                    Soldier_BeeEntity.this.applyPoisonEffect(target);
                }
                Soldier_BeeEntity.this.attackCooldown = 20;
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BEE_LOOP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BEE_DEATH;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }
}
