package com.pixelfabric.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class Octana_ExplodeEntity extends HostileEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final float EXPLOSION_RADIUS = 5.0F;
    private static final float LOW_HEALTH_THRESHOLD = 0.3F;
    private int webShootCooldown = 0;

    public Octana_ExplodeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.experiencePoints = 20;
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 5.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new OctanaLeapAtTargetGoal(this, 0.5F));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    private PlayState movementPredicate(AnimationState<Octana_ExplodeEntity> event) {
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.octanaexplode.walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.octanaexplode.idle", Animation.LoopType.LOOP));
    }

    private PlayState attackPredicate(AnimationState<Octana_ExplodeEntity> event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            event.setAndContinue(RawAnimation.begin().then("animation.octanaexplode.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }
        return PlayState.CONTINUE;
    }

    private PlayState explodePredicate(AnimationState<Octana_ExplodeEntity> event) {
        if (this.getHealth() <= this.getMaxHealth() * LOW_HEALTH_THRESHOLD) {
            return event.setAndContinue(RawAnimation.begin().then("animation.octanaexplode.explode", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attack", 0, this::attackPredicate));
        data.add(new AnimationController<>(this, "explode", 0, this::explodePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.isAlive() && this.getHealth() <= this.getMaxHealth() * LOW_HEALTH_THRESHOLD) {
                this.explode();
            }

            if (this.webShootCooldown > 0) {
                this.webShootCooldown--;
            }

            LivingEntity target = this.getTarget();
            if (target != null && this.webShootCooldown == 0 && this.distanceTo(target) < 10.0) {
                shootWeb(target);
                this.webShootCooldown = 100; // 5 segundos de cooldown
            }
        }
    }

    private void explode() {
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), EXPLOSION_RADIUS, World.ExplosionSourceType.MOB);
            this.discard();
        }
    }

    private void shootWeb(LivingEntity target) {
        Vec3d direction = target.getPos().subtract(this.getPos()).normalize();
        // Aquí deberías crear y lanzar una entidad de telaraña personalizada
        // Por ahora, simplemente aplicaremos un efecto de lentitud al objetivo
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2)); // 5 segundos de lentitud nivel 3
        this.playSound(SoundEvents.ENTITY_SPIDER_AMBIENT, 1.0F, 1.0F);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean wasHurt = super.damage(source, amount);
        if (wasHurt) {
            if (this.getHealth() <= this.getMaxHealth() * LOW_HEALTH_THRESHOLD) {
                this.triggerAnim("explode_controller", "animation.octanaexplode.explode");
            } else {
                // 20% de probabilidad de contraatacar
                if (source.getAttacker() instanceof LivingEntity && this.random.nextFloat() < 0.2F) {
                    this.leap((LivingEntity) source.getAttacker());
                }
            }
        }
        return wasHurt;
    }

    private void leap(LivingEntity target) {
        Vec3d vec3d = new Vec3d(target.getX() - this.getX(), 0.0, target.getZ() - this.getZ());
        if (vec3d.lengthSquared() > 1.0E-7) {
            vec3d = vec3d.normalize().multiply(0.5).add(this.getVelocity().multiply(0.2));
        }
        this.setVelocity(vec3d.x, 0.5, vec3d.z);
        this.playSound(SoundEvents.ENTITY_SPIDER_AMBIENT, 1.0F, 1.0F);
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (super.tryAttack(target)) {
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1)); // 5 segundos de veneno nivel 2
            }
            return true;
        }
        return false;
    }

    private class OctanaLeapAtTargetGoal extends Goal {
        private final Octana_ExplodeEntity octana;
        private LivingEntity target;
        private final float leapMotionY;

        public OctanaLeapAtTargetGoal(Octana_ExplodeEntity octana, float leapMotionY) {
            this.octana = octana;
            this.leapMotionY = leapMotionY;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        @Override
        public boolean canStart() {
            this.target = this.octana.getTarget();
            if (this.target == null) {
                return false;
            } else {
                double d = this.octana.squaredDistanceTo(this.target);
                if (d >= 4.0 && d <= 16.0) {
                    if (!this.octana.isOnGround()) {
                        return false;
                    } else {
                        return this.octana.getRandom().nextInt(5) == 0;
                    }
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            return !this.octana.isOnGround();
        }

        @Override
        public void start() {
            Vec3d vec3d = this.octana.getVelocity();
            Vec3d vec3d2 = new Vec3d(this.target.getX() - this.octana.getX(), 0.0, this.target.getZ() - this.octana.getZ());
            if (vec3d2.lengthSquared() > 1.0E-7) {
                vec3d2 = vec3d2.normalize().multiply(0.4).add(vec3d.multiply(0.2));
            }

            this.octana.setVelocity(vec3d2.x, this.leapMotionY, vec3d2.z);
        }
    }
}