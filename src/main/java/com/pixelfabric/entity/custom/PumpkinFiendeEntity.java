package com.pixelfabric.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PumpkinFiendeEntity extends HostileEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private int vineAttackCooldown = 0;

    public PumpkinFiendeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 25;
        this.setStackInHand(this.getActiveHand(), new ItemStack(Items.TORCH));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 150.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, net.minecraft.entity.passive.VillagerEntity.class, true));
        this.targetSelector.add(3, new RevengeGoal(this));
    }

    private PlayState movementPredicate(AnimationState<PumpkinFiendeEntity> event) {
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.pumpkin.walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.pumpkin.idle", Animation.LoopType.LOOP));
    }

    private PlayState attackPredicate(AnimationState<PumpkinFiendeEntity> event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            event.setAndContinue(RawAnimation.begin().then("animation.pumpkin.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        data.add(new AnimationController<>(this, "attack", 0, this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.vineAttackCooldown > 0) {
                this.vineAttackCooldown--;
            }

            if (this.vineAttackCooldown == 0 && this.getTarget() != null && this.getRandom().nextInt(120) == 0) {
                this.performVineAttack();
            }

            // Efecto de partículas de fuego
            if (this.age % 5 == 0) {
                BlockPos pos = this.getBlockPos().up();
                this.getWorld().addParticle(net.minecraft.particle.ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
            }
        }
    }

    private void performVineAttack() {
        if (this.getTarget() != null && this.getTarget().isOnGround()) {
            // Aquí deberías implementar la lógica para spawner las vines
            // Por ahora, simplemente aplicaremos un efecto de lentitud y daño al objetivo
            this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            this.getTarget().damage(this.getDamageSources().mobAttack(this), 5f);
            this.playSound(SoundEvents.BLOCK_GRASS_BREAK, 1.0F, 1.0F);
            this.vineAttackCooldown = 120;
        }
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof LivingEntity) {
            ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
        }
        return success;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void onDeath(net.minecraft.entity.damage.DamageSource source) {
        super.onDeath(source);
        // Efecto de veneno en el área al morir
        BlockPos pos = this.getBlockPos();
        int radius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = pos.add(x, y, z);
                    if (this.getWorld().getBlockState(targetPos).isAir()) {
                        this.getWorld().addParticle(net.minecraft.particle.ParticleTypes.ENTITY_EFFECT,
                                targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                                0.5, 0.5, 0.5);
                    }
                }
            }
        }

        // Aplicar efecto de veneno a entidades cercanas
        this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(radius),
                        entity -> entity != this && !entity.isSpectator())
                .forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 1)));
    }
}