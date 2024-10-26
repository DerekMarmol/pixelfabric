package com.pixelfabric.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class WraithEntity extends FlyingEntity implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean isAttacking = false;

    public WraithEntity(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return FlyingEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new FlyingAttackGoal(this, 1.2D, false));
        this.goalSelector.add(3, new FlyRandomlyGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            if (isAttacking) {
                return event.setAndContinue(RawAnimation.begin().then("animation.wraith.attack", Animation.LoopType.PLAY_ONCE));
            }
            if (event.isMoving()) {
                return event.setAndContinue(RawAnimation.begin().then("animation.wraith.walk", Animation.LoopType.LOOP));
            }
            return event.setAndContinue(RawAnimation.begin().then("animation.wraith.idle", Animation.LoopType.LOOP));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    static class FlyRandomlyGoal extends Goal {
        private final WraithEntity wraith;

        public FlyRandomlyGoal(WraithEntity wraith) {
            this.wraith = wraith;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return wraith.getNavigation().isIdle() && wraith.getRandom().nextInt(10) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return wraith.getNavigation().isFollowingPath();
        }

        @Override
        public void start() {
            Vec3d vec3d = this.getRandomLocation();
            if (vec3d != null) {
                wraith.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
        }

        private Vec3d getRandomLocation() {
            Vec3d vec3d = wraith.getRotationVec(0.0F);
            Vec3d vec3d2 = Vec3d.ofBottomCenter(wraith.getBlockPos().add(
                    wraith.getRandom().nextInt(7) - 3,
                    wraith.getRandom().nextInt(7) - 3,
                    wraith.getRandom().nextInt(7) - 3
            ));
            return vec3d2;
        }
    }

    static class FlyingAttackGoal extends Goal {
        private final WraithEntity wraith;
        private final double speed;
        private final boolean pauseWhenMobIdle;
        private int attackTick;
        private int updateCountdownTicks;

        public FlyingAttackGoal(WraithEntity wraith, double speed, boolean pauseWhenMobIdle) {
            this.wraith = wraith;
            this.speed = speed;
            this.pauseWhenMobIdle = pauseWhenMobIdle;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity target = wraith.getTarget();
            return target != null && target.isAlive() && wraith.canTarget(target);
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity target = wraith.getTarget();
            return target != null && target.isAlive() && wraith.canTarget(target) &&
                    (!pauseWhenMobIdle || !wraith.getNavigation().isIdle());
        }

        @Override
        public void start() {
            wraith.setAttacking(true);
            this.attackTick = 0;
        }

        @Override
        public void stop() {
            super.stop();
            wraith.isAttacking = false;
        }

        @Override
        public void tick() {
            LivingEntity target = wraith.getTarget();
            if (target == null) return;

            wraith.getLookControl().lookAt(target, 30.0F, 30.0F);
            double distanceSquared = wraith.squaredDistanceTo(target);
            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);

            if ((this.pauseWhenMobIdle || wraith.getVisibilityCache().canSee(target)) &&
                    this.updateCountdownTicks <= 0 &&
                    (wraith.getX() == wraith.prevX && wraith.getY() == wraith.prevY && wraith.getZ() == wraith.prevZ ||
                            target.squaredDistanceTo(wraith.prevX, wraith.prevY, wraith.prevZ) >= 1.0 ||
                            wraith.getRandom().nextFloat() < 0.05F)) {
                wraith.getNavigation().startMovingTo(target, this.speed);
                this.updateCountdownTicks = 4 + wraith.getRandom().nextInt(7);

                if (distanceSquared > 1024.0) {
                    this.updateCountdownTicks += 10;
                } else if (distanceSquared > 256.0) {
                    this.updateCountdownTicks += 5;
                }
            }

            this.attackTick = Math.max(this.attackTick - 1, 0);
            this.checkAndPerformAttack(target, distanceSquared);
        }

        protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.attackTick <= 0) {
                this.attackTick = 20;
                wraith.swingHand(Hand.MAIN_HAND);
                wraith.tryAttack(target);
                wraith.isAttacking = true;
            } else {
                wraith.isAttacking = false;
            }
        }

        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return wraith.getWidth() * 2.0F * wraith.getWidth() * 2.0F + entity.getWidth();
        }
    }
}