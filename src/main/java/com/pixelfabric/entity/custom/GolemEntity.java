package com.pixelfabric.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GolemEntity extends HostileEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown = 0;
    private int specialAttackCooldown = 0;  // Cooldown para el ataque especial.
    private boolean readyForSpecialAttack = false;  // Controla si el ataque especial está listo.

    public GolemEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0D)  // Aumentamos la vida.
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0f)  // Aumentamos el daño de ataque.
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)  // Un poco más lento pero más resistente.
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0f)  // Mejor knockback.
                .add(EntityAttributes.GENERIC_ARMOR, 15.0D);  // Añadimos armadura para resistir más.
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
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.golem.walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.golem.idle_2", Animation.LoopType.LOOP));
    }

    private PlayState attackPredicate(AnimationState<GolemEntity> event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            // Selecciona la animación según si es un ataque especial o normal
            if (readyForSpecialAttack) {
                event.setAndContinue(RawAnimation.begin().then("animation.golem.spin", Animation.LoopType.PLAY_ONCE));
                readyForSpecialAttack = false;  // Desactiva el ataque especial hasta que esté listo nuevamente.
            } else {
                event.setAndContinue(RawAnimation.begin().then("animation.golem.attack", Animation.LoopType.PLAY_ONCE));
            }
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
        if (this.specialAttackCooldown > 0) {
            this.specialAttackCooldown--;
        } else {
            this.readyForSpecialAttack = true;  // Listo para hacer el ataque especial.
            this.specialAttackCooldown = 200;  // Reinicia el cooldown del ataque especial (10 segundos, si 20 ticks = 1 segundo).
        }
    }

    private class GolemAttackGoal extends MeleeAttackGoal {
        public GolemAttackGoal(GolemEntity golem, double speed, boolean pauseWhenMobIdle) {
            super(golem, speed, pauseWhenMobIdle);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && GolemEntity.this.attackCooldown == 0;
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.getCooldown() <= 0) {
                this.resetCooldown();
                this.mob.swingHand(Hand.MAIN_HAND);
                this.mob.tryAttack(target);
                GolemEntity.this.attackCooldown = 20;  // 1 segundo de cooldown normal.
            }
        }
    }
}
