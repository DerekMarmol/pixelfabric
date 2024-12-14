package com.pixelfabric.entity.custom;

import com.pixelfabric.network.FlashEffectPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CandikEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown = 0;
    private boolean isVanishing = false;
    private int vanishTimer = 0;

    public CandikEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new CandikAttackGoal(this, 1.0D, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    private PlayState predicate(AnimationState<CandikEntity> event) {
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then(
                    this.getVelocity().lengthSquared() > 0.3 ?
                            "animation.candik.run" : "animation.candik.walk",
                    Animation.LoopType.LOOP
            ));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.candik.idle", Animation.LoopType.LOOP));
    }

    private PlayState attackPredicate(AnimationState<CandikEntity> event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            event.setAndContinue(RawAnimation.begin().then("animation.candik.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, this::predicate));
        // Aumenté la velocidad de animación a 2.0 (doble de velocidad)
        data.add(new AnimationController<>(this, "attack_controller", 0, this::attackPredicate)
                .setAnimationSpeed(2.5f));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (attackCooldown > 0) {
                attackCooldown--;
            }

            if (isVanishing) {
                vanishTimer++;
                if (vanishTimer >= 30) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (this.attackCooldown <= 0) {
            if (super.tryAttack(target)) {
                if (target instanceof ServerPlayerEntity player) {
                    FlashEffectPacket.send(player);
                    this.isVanishing = true;
                    this.vanishTimer = 0;
                }
                this.attackCooldown = 40;
                return true;
            }
        }
        return false;
    }

    private class CandikAttackGoal extends MeleeAttackGoal {
        private final CandikEntity candik;

        public CandikAttackGoal(CandikEntity candik, double speed, boolean pauseWhenMobIdle) {
            super(candik, speed, pauseWhenMobIdle);
            this.candik = candik;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && candik.attackCooldown == 0;
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.getCooldown() <= 0) {
                this.resetCooldown();
                this.mob.swingHand(Hand.MAIN_HAND);
                this.mob.tryAttack(target);
            }
        }
    }
}