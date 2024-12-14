package com.pixelfabric.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Lava_SquidEntity extends PathAwareEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int fireballCooldown = 0;
    private int fireballBurst = 0;
    private static final int FIREBALL_COOLDOWN = 30; // 1.5 segundos (30 ticks)
    private static final int BURST_COUNT = 2; // Número de bolas de fuego por ráfaga

    public Lava_SquidEntity(EntityType<? extends MobEntity> entityType, World world) {
        super((EntityType<? extends PathAwareEntity>) entityType, world);
        this.setNoGravity(true);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LavaSquidAttackGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean canWalkOnFluid(FluidState state) {
        return state.isIn(FluidTags.LAVA);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            // Manejo del movimiento en lava
            if (this.isInLava()) {
                this.setVelocity(this.getVelocity().multiply(0.9));
                if (this.getVelocity().y < 0) {
                    this.setVelocity(this.getVelocity().multiply(1, 0.8, 1));
                }
            }

            // Manejo del cooldown de bolas de fuego
            if (fireballCooldown > 0) {
                fireballCooldown--;
            }
        }
    }

    private void shootFireball(LivingEntity target) {
        double d = this.getX() - target.getX();
        double e = this.getBodyY(0.5D) - target.getEyeY();
        double f = this.getZ() - target.getZ();

        SmallFireballEntity fireball = new SmallFireballEntity(
                this.getWorld(),
                this,
                d,
                e,
                f
        );

        fireball.setPosition(
                this.getX(),
                this.getBodyY(0.5D),
                this.getZ()
        );

        this.getWorld().spawnEntity(fireball);
    }

    private PlayState predicate(AnimationState<Lava_SquidEntity> event) {
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.lava_squid.swim", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.lava_squid.idle", Animation.LoopType.LOOP));
    }

    private PlayState attackPredicate(AnimationState<Lava_SquidEntity> event) {
        if (this.fireballCooldown == FIREBALL_COOLDOWN) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().then("animation.lava_squid.shoot", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, this::predicate));
        data.add(new AnimationController<>(this, "attack_controller", 0, this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.fireballCooldown = nbt.getInt("FireballCooldown");
        this.fireballBurst = nbt.getInt("FireballBurst");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("FireballCooldown", this.fireballCooldown);
        nbt.putInt("FireballBurst", this.fireballBurst);
    }

    private class LavaSquidAttackGoal extends Goal {
        private final Lava_SquidEntity squid;
        private LivingEntity target;
        private int burstCooldown = 0;

        public LavaSquidAttackGoal(Lava_SquidEntity squid) {
            this.squid = squid;
        }

        @Override
        public boolean canStart() {
            this.target = this.squid.getTarget();
            return this.target != null && this.target.isAlive() && this.squid.canSee(this.target);
        }

        @Override
        public void tick() {
            if (this.target == null) return;

            // Mantener distancia con el objetivo
            double distance = this.squid.squaredDistanceTo(this.target);
            if (distance < 4.0D) {
                Vec3d awayVector = this.squid.getPos().subtract(this.target.getPos()).normalize();
                this.squid.setVelocity(awayVector.x * 0.3, awayVector.y * 0.3, awayVector.z * 0.3);
            }

            // Lógica de disparo de bolas de fuego
            if (squid.fireballCooldown <= 0) {
                if (burstCooldown <= 0) {
                    squid.shootFireball(target);
                    squid.fireballBurst++;

                    if (squid.fireballBurst >= BURST_COUNT) {
                        squid.fireballBurst = 0;
                        squid.fireballCooldown = FIREBALL_COOLDOWN;
                    } else {
                        burstCooldown = 5; // 0.25 segundos entre disparos de la misma ráfaga
                    }
                } else {
                    burstCooldown--;
                }
            }

            // Mirar al objetivo
            this.squid.getLookControl().lookAt(target, 30.0F, 30.0F);
        }
    }
}