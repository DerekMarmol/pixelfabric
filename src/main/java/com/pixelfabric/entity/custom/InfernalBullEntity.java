package com.pixelfabric.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
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

public class InfernalBullEntity extends HostileEntity implements GeoEntity {
    private static final TrackedData<Boolean> SHOOT = DataTracker.registerData(InfernalBullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> ANIMATION = DataTracker.registerData(InfernalBullEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> TEXTURE = DataTracker.registerData(InfernalBullEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> STUNNED = DataTracker.registerData(InfernalBullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";
    private String prevAnim = "empty";
    private int stunTimer = 0;
    private static final int STUN_DURATION = 60; // 3 segundos (60 ticks)
    private boolean isCharging = false;

    public InfernalBullEntity(EntityType<? extends InfernalBullEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(0.6F);
        this.experiencePoints = 0;
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOT, false);
        this.dataTracker.startTracking(ANIMATION, "undefined");
        this.dataTracker.startTracking(TEXTURE, "infernal_bull");
        this.dataTracker.startTracking(STUNNED, false);
    }

    public boolean isStunned() {
        return this.dataTracker.get(STUNNED);
    }

    public void setStunned(boolean stunned) {
        this.dataTracker.set(STUNNED, stunned);
        if (stunned) {
            this.stunTimer = STUN_DURATION;
            this.animationprocedure = "tieso";
        }
    }

    public void setTexture(String texture) {
        this.dataTracker.set(TEXTURE, texture);
    }

    public String getTexture() {
        return this.dataTracker.get(TEXTURE);
    }

    @Override
    protected void initGoals() {
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true, true));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.8D, false) {
            @Override
            protected double getSquaredMaxAttackDistance(LivingEntity entity) {
                return 6.25;
            }

            @Override
            public boolean canStart() {
                return !isStunned() && attackCooldown <= 0 && super.canStart();
            }

            @Override
            protected void attack(LivingEntity target, double squaredDistance) {
                if (squaredDistance <= this.getSquaredMaxAttackDistance(target)) {
                    this.resetCooldown();
                    attackCooldown = 40;
                    isCharging = true;
                    tryAttack(target);
                }
            }
        });

        this.targetSelector.add(3, new RevengeGoal(this));
        this.goalSelector.add(4, new WanderAroundGoal(this, 0.8));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 150.0)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.7)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 4.0);
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (isStunned()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("tieso"));
            }

            if (!event.isMoving() && event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F) {
                if (this.isOnGround()) {
                    if (this.isCharging) {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("carga"));
                    } else {
                        return this.isDead() ?
                                event.setAndContinue(RawAnimation.begin().thenLoop("tieso")) :
                                event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
                    }
                }
                return event.setAndContinue(RawAnimation.begin().thenPlay("tieso"));
            } else {
                return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
            }
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        double dx = this.getX() - this.prevX;
        double dz = this.getZ() - this.prevZ;
        float velocity = (float) Math.sqrt(dx * dx + dz * dz);

        if (this.handSwinging && !this.swinging) {
            this.swinging = true;
            this.lastSwing = this.getWorld().getTime();
        }

        if (this.swinging && this.lastSwing + 7L <= this.getWorld().getTime()) {
            this.swinging = false;
        }

        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("attack"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (isStunned()) {
            return false;
        }

        if (!super.tryAttack(target)) {
            return false;
        }

        if (target instanceof PlayerEntity player) {
            double xDiff = player.getX() - this.getX();
            double zDiff = player.getZ() - this.getZ();
            double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

            if (distance > 0) {
                xDiff /= distance;
                zDiff /= distance;
            }

            double upwardForce = 0.8;
            double horizontalForce = 2.5;

            player.setVelocity(
                    xDiff * horizontalForce,
                    upwardForce,
                    zDiff * horizontalForce
            );

            player.velocityModified = true;
            this.animationprocedure = "attack";
            this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK, 1.0F, 1.0F);
        }

        return true;
    }

    // Optional: Add a cooldown system for the attack
    private int attackCooldown = 3;

    public void checkBlockCollision() {
        if (isCharging && !isStunned()) {
            BlockPos frontPos = this.getBlockPos().offset(this.getMovementDirection());
            if (!this.getWorld().getBlockState(frontPos).isAir()) {
                // Colisión con bloque detectada
                this.setStunned(true);
                isCharging = false;
                this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0F, 0.5F);

                // Detener el movimiento
                this.setVelocity(0, 0, 0);
                this.velocityModified = true;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (isStunned()) {
            if (stunTimer > 0) {
                stunTimer--;
                // Evitar cualquier movimiento mientras está aturdido
                this.setVelocity(0, this.getVelocity().y, 0);
            } else {
                setStunned(false);
                animationprocedure = "empty";
            }
        }

        checkBlockCollision();
    }

    private PlayState procedurePredicate(AnimationState event) {
        if (!this.animationprocedure.equals(this.prevAnim)) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
        }

        if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            this.animationprocedure = "empty";
            event.getController().forceAnimationReset();
        }

        this.prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("Texture", this.getTexture());
        nbt.putBoolean("Stunned", this.isStunned());
        nbt.putInt("StunTimer", this.stunTimer);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Texture")) {
            this.setTexture(nbt.getString("Texture"));
        }
        if (nbt.contains("Stunned")) {
            this.setStunned(nbt.getBoolean("Stunned"));
        }
        if (nbt.contains("StunTimer")) {
            this.stunTimer = nbt.getInt("StunTimer");
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HOGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HOGLIN_DEATH;
    }
}