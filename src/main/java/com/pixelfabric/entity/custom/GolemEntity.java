package com.pixelfabric.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class GolemEntity extends HostileEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown = 0;
    private int specialAttackCooldown = 0;
    private boolean readyForSpecialAttack = false;
    private boolean isSpinning = false;
    private int spinDuration = 0;
    private static final int SPIN_ATTACK_DURATION = 20;
    private static final float SPIN_ATTACK_RADIUS = 3.0f;
    private LivingEntity currentTarget = null;

    public GolemEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 15.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new GolemAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
    }

    private PlayState predicate(AnimationState<GolemEntity> event) {
        if (this.handSwinging) {
            return PlayState.STOP;
        }

        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.golem.walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.golem.idle_2", Animation.LoopType.LOOP));
    }

    private PlayState attackPredicate(AnimationState<GolemEntity> event) {
        if (this.handSwinging) {
            if (readyForSpecialAttack) {
                isSpinning = true;
                spinDuration = SPIN_ATTACK_DURATION;
                readyForSpecialAttack = false;
                return event.setAndContinue(RawAnimation.begin().then("animation.golem.spin", Animation.LoopType.PLAY_ONCE));
            } else {
                return event.setAndContinue(RawAnimation.begin().then("animation.golem.attack", Animation.LoopType.PLAY_ONCE));
            }
        }
        return PlayState.STOP;
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

    private void applySpinDamage() {
        if (!this.getWorld().isClient && isSpinning) {
            Box damageBox = this.getBoundingBox().expand(SPIN_ATTACK_RADIUS);
            List<Entity> nearbyEntities = this.getWorld().getOtherEntities(this, damageBox);

            float spinDamage = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity && !(entity instanceof GolemEntity)) {
                    LivingEntity target = (LivingEntity) entity;

                    // Aplicar daño
                    boolean damaged = target.damage(this.getDamageSources().mobAttack(this), spinDamage);

                    if (damaged) {
                        // Knockback más fuerte
                        double dx = entity.getX() - this.getX();
                        double dz = entity.getZ() - this.getZ();
                        double strength = 0.7;

                        Vec3d velocity = new Vec3d(dx, 0.0, dz).normalize().multiply(strength);
                        entity.setVelocity(velocity.x, 0.4, velocity.z);
                        entity.velocityModified = true;

                        // Efectos visuales
                        ((ServerWorld) this.getWorld()).spawnParticles(
                                ParticleTypes.CRIT,
                                entity.getX(),
                                entity.getY() + entity.getHeight() / 2,
                                entity.getZ(),
                                10, 0.2, 0.2, 0.2, 0.1
                        );
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        if (this.specialAttackCooldown > 0) {
            this.specialAttackCooldown--;
        } else {
            this.readyForSpecialAttack = true;
            this.specialAttackCooldown = 200;
        }

        if (isSpinning) {
            applySpinDamage();
            spinDuration--;
            if (spinDuration <= 0) {
                isSpinning = false;
                this.handSwinging = false;
            }
        }
    }

    private class GolemAttackGoal extends MeleeAttackGoal {
        private final GolemEntity golem;

        public GolemAttackGoal(GolemEntity golem, double speed, boolean pauseWhenMobIdle) {
            super(golem, speed, pauseWhenMobIdle);
            this.golem = golem;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && golem.attackCooldown == 0;
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.getCooldown() <= 0) {
                this.resetCooldown();
                currentTarget = target;

                // Inicia la animación
                golem.handSwinging = true;

                // Aplica el daño
                if (!isSpinning) {
                    this.mob.tryAttack(target);
                    golem.attackCooldown = 20;
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            currentTarget = null;
        }
    }

    // Override del método tryAttack para asegurar que el daño se aplique correctamente
    @Override
    public boolean tryAttack(Entity target) {
        if (isSpinning) {
            return false; // El daño del spin se maneja separadamente
        }
        return super.tryAttack(target);
    }
}
