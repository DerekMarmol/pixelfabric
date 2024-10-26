package com.pixelfabric.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class OwlbearEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossBar bossBar;

    // Attack cooldowns and states
    private int attackCooldown = 0;
    private int specialAttackCooldown = 0;
    private boolean isEnraged = false;
    private static final int ENRAGE_HEALTH_THRESHOLD = 100;
    private static final double ATTACK_RANGE = 2.5D;

    private String currentAnimation = ANIM_IDLE;
    private boolean isAttacking = false;
    private int animationTicks = 0;
    private int fidgetTimer = 0;

    // Animation states
    private static final String ANIM_IDLE = "animation.owlbear.idle";
    private static final String ANIM_WALK = "animation.owlbear.walk";
    private static final String ANIM_RUN = "animation.owlbear.run";
    private static final String ANIM_SCREECH = "animation.owlbear.screech";
    private static final String ANIM_COMBO = "animation.owlbear.combo_1";
    private static final String ANIM_SWIPE_1 = "animation.owlbear.swipe_down_1";
    private static final String ANIM_SWIPE_2 = "animation.owlbear.swipe_down_2";
    private static final String ANIM_BITE = "animation.owlbear.bite_down";
    private static final String ANIM_DEATH = "animation.owlbear.death";
    private static final String ANIM_FIDGET = "animation.owlbear.fidget";

    public OwlbearEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 50;
        this.bossBar = new ServerBossBar(
                Text.literal("El Defensor del Bosque Ursúho"),
                BossBar.Color.RED,
                BossBar.Style.PROGRESS
        );
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 250.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new OwlbearAttackGoal(this));
        this.goalSelector.add(3, new CustomRetreatGoal(this));
        this.goalSelector.add(4, new CustomCircleTargetGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    private class CustomRetreatGoal extends Goal {
        private final OwlbearEntity owlbear;
        private final double retreatSpeed;
        private static final float RETREAT_HEALTH_THRESHOLD = 0.3f;
        private Vec3d retreatPosition;

        public CustomRetreatGoal(OwlbearEntity owlbear) {
            this.owlbear = owlbear;
            this.retreatSpeed = 0.7D;
        }

        @Override
        public boolean canStart() {
            return owlbear.getHealth() / owlbear.getMaxHealth() < RETREAT_HEALTH_THRESHOLD
                    && owlbear.getTarget() != null;
        }

        @Override
        public void start() {
            LivingEntity target = owlbear.getTarget();
            if (target != null) {
                Vec3d targetPos = target.getPos();
                Vec3d owlbearPos = owlbear.getPos();
                Vec3d direction = owlbearPos.subtract(targetPos).normalize();
                retreatPosition = owlbearPos.add(direction.multiply(10));
            }
        }

        @Override
        public void tick() {
            if (retreatPosition != null) {
                owlbear.getNavigation().startMovingTo(
                        retreatPosition.x,
                        retreatPosition.y,
                        retreatPosition.z,
                        retreatSpeed
                );
            }
        }
    }

    private class OwlbearAttackGoal extends MeleeAttackGoal {
        private final OwlbearEntity owlbear;

        public OwlbearAttackGoal(OwlbearEntity owlbear) {
            super(owlbear, 1.2D, false);
            this.owlbear = owlbear;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && owlbear.attackCooldown <= 0;
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            if (squaredDistance <= ATTACK_RANGE * ATTACK_RANGE && this.getCooldown() <= 0) {
                this.resetCooldown();
                if (!isAttacking) {
                    chooseAndPerformAttack(target);
                }
            }
        }
    }

    private void chooseAndPerformAttack(LivingEntity target) {
        if (attackCooldown > 0 || target == null) return;

        double random = this.random.nextDouble();

        if (this.getHealth() <= ENRAGE_HEALTH_THRESHOLD) {
            if (random < 0.4) {
                performEnragedSlash(target);
            } else if (random < 0.7) {
                performClawCombo(target);
            } else {
                performRoar();
            }
        } else {
            if (random < 0.4) {
                performBasicSlash(target);
            } else if (random < 0.7) {
                performBite(target);
            } else {
                performClawCombo(target);
            }
        }

        attackCooldown = 20;
    }

    private void performBasicSlash(LivingEntity target) {
        setAnimation(ANIM_SWIPE_1, 20);
        if (isWithinMeleeAttackRange(target)) {
            target.damage(this.getDamageSources().mobAttack(this), 12.0F);
            applyKnockback(target, 0.5F);
        }
    }

    private void performEnragedSlash(LivingEntity target) {
        setAnimation(ANIM_SWIPE_2, 20);
        if (isWithinMeleeAttackRange(target)) {
            target.damage(this.getDamageSources().mobAttack(this), 18.0F);
            applyKnockback(target, 1.0F);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
        }
    }

    private void performBite(LivingEntity target) {
        setAnimation(ANIM_BITE, 15);
        if (isWithinMeleeAttackRange(target)) {
            target.damage(this.getDamageSources().mobAttack(this), 15.0F);
            this.heal(5.0F);
        }
    }

    private void performClawCombo(LivingEntity target) {
        setAnimation(ANIM_COMBO, 30);
        if (isWithinMeleeAttackRange(target)) {
            // First hit
            target.damage(this.getDamageSources().mobAttack(this), 8.0F);

            // Schedule second hit
            this.getWorld().getServer().execute(() -> {
                if (isAlive() && target.isAlive() && isWithinMeleeAttackRange(target)) {
                    target.damage(this.getDamageSources().mobAttack(this), 8.0F);
                    applyKnockback(target, 0.8F);
                }
            });
        }
    }

    private class CustomCircleTargetGoal extends Goal {
        private final OwlbearEntity owlbear;
        private final double speed;
        private double angle = 0;
        private static final double CIRCLE_RADIUS = 5.0;

        public CustomCircleTargetGoal(OwlbearEntity owlbear) {
            this.owlbear = owlbear;
            this.speed = 1.0D;
        }

        @Override
        public boolean canStart() {
            return owlbear.getTarget() != null && !owlbear.isAttacking;
        }

        @Override
        public void tick() {
            LivingEntity target = owlbear.getTarget();
            if (target != null) {
                angle += 0.1;
                if (angle > Math.PI * 2) angle = 0;

                double targetX = target.getX() + Math.cos(angle) * CIRCLE_RADIUS;
                double targetZ = target.getZ() + Math.sin(angle) * CIRCLE_RADIUS;

                owlbear.getNavigation().startMovingTo(targetX, target.getY(), targetZ, speed);
            }
        }
    }

    private void performRoar() {
        setAnimation(ANIM_SCREECH, 40);
        this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0F, 1.0F);

        this.getWorld().getNonSpectatingEntities(LivingEntity.class,
                this.getBoundingBox().expand(5.0D, 2.0D, 5.0D)).forEach(entity -> {
            if (entity != this) {
                entity.damage(this.getDamageSources().mobAttack(this), 8.0F);
                applyKnockback(entity, 1.2F);
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));
            }
        });
    }

    private boolean isWithinMeleeAttackRange(LivingEntity target) {
        return this.squaredDistanceTo(target) <= ATTACK_RANGE * ATTACK_RANGE;
    }

    private void applyKnockback(LivingEntity target, float strength) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double magnitude = Math.sqrt(dx * dx + dz * dz);
        if (magnitude != 0) {
            target.addVelocity(
                    (dx / magnitude) * strength,
                    0.2,
                    (dz / magnitude) * strength
            );
        }
    }

    private void setAnimation(String animation) {
        if (!currentAnimation.equals(animation)) {
            currentAnimation = animation;
            triggerAnim("controller", animation);

            // Añadir efectos de sonido específicos para cada animación
            playAnimationSound(animation);
        }
    }

    private void playAnimationSound(String animation) {
        switch (animation) {
            case "swipe_down_1":
                this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F);
                break;
            case "swipe_down_2":
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
                break;
            case "bite_down":
                this.playSound(SoundEvents.ENTITY_WOLF_GROWL, 1.0F, 1.0F);
                break;
            case "screech":
                this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0F, 1.0F);
                break;
            default:
                break;
        }
    }

    private void updateAnimationState() {
        if (!isAttacking) {
            Vec3d velocity = this.getVelocity();
            double horizontalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

            if (horizontalSpeed > 0.1) {
                setAnimation(isEnraged ? ANIM_RUN : ANIM_WALK, -1);
            } else if (currentAnimation.equals(ANIM_WALK) || currentAnimation.equals(ANIM_RUN)) {
                resetAnimation();
            }
        }
    }

    private void resetAnimation() {
        setAnimation(ANIM_IDLE, -1);
    }

    private void setAnimation(String animation, int ticks) {
        if (!currentAnimation.equals(animation)) {
            currentAnimation = animation;
            animationTicks = ticks;

            // Si es una animación de ataque
            if (animation.contains("swipe") || animation.contains("bite") ||
                    animation.contains("combo") || animation.contains("screech")) {
                isAttacking = true;
            }
        }
    }


    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            // Update animation states
            updateAnimationState();

            // Handle attack cooldowns
            if (attackCooldown > 0) {
                attackCooldown--;
            }
            if (specialAttackCooldown > 0) {
                specialAttackCooldown--;
            }

            // Handle animation timing
            if (animationTicks > 0) {
                animationTicks--;
                if (animationTicks <= 0) {
                    isAttacking = false;
                    resetAnimation();
                }
            }

            // Random fidget animation
            if (!isAttacking) {
                fidgetTimer++;
                if (fidgetTimer >= 200 && random.nextFloat() < 0.1f) {
                    setAnimation(ANIM_FIDGET, 40);
                    fidgetTimer = 0;
                }
            }
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean damaged = super.damage(source, amount);
        if (damaged) {
            if (this.getHealth() <= ENRAGE_HEALTH_THRESHOLD && !this.isEnraged) {
                this.isEnraged = true;
                setAnimation(ANIM_SCREECH, 40);
                this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 1));
            }
            return true;
        }
        return false;
    }

    // GeckoLib Animation Handling
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(
                this,
                "controller",
                0,
                state -> {
                    state.getController().setAnimation(RawAnimation.begin().thenPlay(currentAnimation));
                    return PlayState.CONTINUE;
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
    }
}
