package com.pixelfabric.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WildfireEntity extends BlazeEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown = 0;
    private boolean isSpinAttacking = false;
    private int spinAttackTimer = 0;
    private int teleportCooldown = 0;

    public WildfireEntity(EntityType<? extends BlazeEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 15;
    }

    public static DefaultAttributeContainer.Builder createWildfireAttributes() {
        return BlazeEntity.createBlazeAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 70.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 15.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 3.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new WildfireAttackGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, (new RevengeGoal(this)).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        if (this.isSpinAttacking) {
            this.spinAttackTimer++;
            if (this.spinAttackTimer >= 20) {
                this.isSpinAttacking = false;
                this.spinAttackTimer = 0;
                this.createFireRing();
            }
        }
        if (this.teleportCooldown > 0) {
            this.teleportCooldown--;
        }
        if (this.isOnFire()) {
            this.heal(0.5F);
        }
    }

    public void shootFireballs() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        Vec3d vec3d = this.getRotationVec(1.0F);
        double d = target.getX() - (this.getX() + vec3d.x * 4.0D);
        double e = target.getBoundingBox().minY + (double)(target.getHeight() / 2.0F) - (0.5D + this.getY() + (double)(this.getHeight() / 2.0F));
        double f = target.getZ() - (this.getZ() + vec3d.z * 4.0D);

        Vec3d perpendicular = new Vec3d(-vec3d.z, 0, vec3d.x).normalize();

        for (int i = -1; i <= 1; i++) {
            Vec3d offset = perpendicular.multiply(i * 0.5);
            SmallFireballEntity smallFireballEntity = new SmallFireballEntity(this.getWorld(), this, d, e, f);
            smallFireballEntity.setPosition(
                    this.getX() + vec3d.x * 2.0D + offset.x,
                    this.getY() + (double)(this.getHeight() / 2.0F) + 0.5D,
                    this.getZ() + vec3d.z * 2.0D + offset.z
            );
            smallFireballEntity.setVelocity(d, e, f, 2.0F, 1.0F);
            this.getWorld().spawnEntity(smallFireballEntity);
        }

        this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    public void spinAttack() {
        if (!this.isSpinAttacking) {
            this.isSpinAttacking = true;
            this.spinAttackTimer = 0;

            this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(3.0D),
                            entity -> entity != this && entity.isAlive() && !entity.isInvulnerable())
                    .forEach(entity -> {
                        entity.damage(this.getDamageSources().mobAttack(this), (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.5F);
                        entity.setOnFireFor(5);
                        double knockbackStrength = 1.5;
                        double dx = entity.getX() - this.getX();
                        double dz = entity.getZ() - this.getZ();
                        double distance = Math.sqrt(dx * dx + dz * dz);
                        entity.addVelocity((dx / distance) * knockbackStrength, 0.5, (dz / distance) * knockbackStrength);
                    });
        }
    }

    private void createFireRing() {
        int radius = 3;
        BlockPos center = this.getBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    BlockPos pos = center.add(x, 0, z);
                    if (this.getWorld().getBlockState(pos).isAir() && this.getWorld().getBlockState(pos.down()).isSolidBlock(this.getWorld(), pos.down())) {
                        this.getWorld().setBlockState(pos, net.minecraft.block.Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.teleportCooldown <= 0 && !source.isSourceCreativePlayer() && this.getRandom().nextFloat() < 0.3F) {
            this.teleport();
        }
        return super.damage(source, amount);
    }

    private void teleport() {
        double d = this.getX() + (this.getRandom().nextDouble() - 0.5D) * 16.0D;
        double e = this.getY() + (double)(this.getRandom().nextInt(16) - 8);
        double f = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * 16.0D;
        this.teleport(d, e, f);
        this.teleportCooldown = 100;
    }

    private PlayState predicate(AnimationState<WildfireEntity> event) {
        if (event.isMoving() || this.isAttacking()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.wildfire.walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.wildfire.idle", Animation.LoopType.LOOP));
    }

    private PlayState attackPredicate(AnimationState<WildfireEntity> event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            event.setAndContinue(RawAnimation.begin().then("animation.wildfire.spin_attack", Animation.LoopType.PLAY_ONCE));
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

    public class WildfireAttackGoal extends Goal {
        private final WildfireEntity wildfire;
        private int attackTime;

        public WildfireAttackGoal(WildfireEntity wildfire) {
            this.wildfire = wildfire;
        }

        @Override
        public boolean canStart() {
            return this.wildfire.getTarget() != null;
        }

        @Override
        public void start() {
            this.attackTime = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.wildfire.getTarget();
            if (target == null) return;

            double distanceSquared = this.wildfire.squaredDistanceTo(target);
            boolean canSee = this.wildfire.getVisibilityCache().canSee(target);

            this.attackTime++;

            if (distanceSquared < 9.0D && canSee && this.attackTime >= 10) {
                this.wildfire.spinAttack();
                this.attackTime = -30;
            } else if (distanceSquared < 256.0D && canSee && this.attackTime >= 30) {
                this.wildfire.shootFireballs();
                this.attackTime = 0;
            }

            this.wildfire.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 1.2D);
        }
    }
}