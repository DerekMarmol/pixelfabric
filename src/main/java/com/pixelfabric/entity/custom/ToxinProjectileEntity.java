package com.pixelfabric.entity.custom;

import com.pixelfabric.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ToxinProjectileEntity extends ThrownItemEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ToxinProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ToxinProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.TOXIN_PROJECTILE, owner, world);
    }

    public ToxinProjectileEntity(World world, double x, double y, double z) {
        super(ModEntities.TOXIN_PROJECTILE, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SLIME_BALL; // Item por defecto, no se usa realmente
    }

    @Override
    public void tick() {
        super.tick();

        // Crear partículas de veneno mientras vuela
        if (this.getWorld().isClient) {
            for (int i = 0; i < 2; i++) {
                this.getWorld().addParticle(
                        ParticleTypes.ITEM_SLIME,
                        this.getX() + (this.random.nextDouble() - 0.5D) * 0.5D,
                        this.getY() + (this.random.nextDouble() - 0.5D) * 0.5D,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * 0.5D,
                        0, 0, 0
                );
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            // Crear área de efecto al impactar
            this.createToxicArea();
            this.playSound(SoundEvents.BLOCK_SLIME_BLOCK_BREAK, 1.0F, 1.0F);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity && !(entity instanceof ToxinSpiderEntity)) {
            // Daño directo
            entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 6.0F);

            // Aplicar efectos venenosos fuertes por impacto directo
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 160, 2)); // Veneno III por 8 segundos
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0)); // Nauseas por 5 segundos
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 1)); // Lentitud II por 4 segundos
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        // Solo crear área tóxica, el proyectil se destruye en onCollision
    }

    private void createToxicArea() {
        // Crear área de efecto tóxico de 3x3
        this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(3.0D),
                        entity -> entity.isAlive() && !(entity instanceof ToxinSpiderEntity))
                .forEach(entity -> {
                    // Efectos más suaves para área de efecto
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 80, 1)); // Veneno II por 4 segundos
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 0)); // Lentitud por 3 segundos
                });

        // Efectos visuales
        if (this.getWorld().isClient) {
            for (int i = 0; i < 20; i++) {
                this.getWorld().addParticle(
                        ParticleTypes.ITEM_SLIME,
                        this.getX() + (this.random.nextDouble() - 0.5D) * 6.0D,
                        this.getY() + this.random.nextDouble() * 2.0D,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * 6.0D,
                        0, 0.1D, 0
                );
            }
        }
    }

    // Animaciones para el proyectil
    private PlayState predicate(AnimationState<ToxinProjectileEntity> state) {
        state.getController().setAnimation(RawAnimation.begin().then("fly", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}