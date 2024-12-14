package com.pixelfabric.mixin;

import com.pixelfabric.commands.SkeletonAccuracyMechanic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class SkeletonAccuracyMixin {
    private static final float VELOCITY_SPREAD_FACTOR = 0.2F;
    private static final float DAMAGE_MULTIPLIER = 1.2F;
    private static final float ARROW_VELOCITY = 1.6F;
    private static final float TARGET_HEIGHT_FACTOR = 0.3333333333333333F;

    @Inject(
            method = "attack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onAttack(LivingEntity target, float pullProgress, CallbackInfo ci) {
        AbstractSkeletonEntity skeleton = (AbstractSkeletonEntity) (Object) this;

        if (SkeletonAccuracyMechanic.isSkeletonAccuracyActive()) {
            ci.cancel();

            ItemStack itemStack = skeleton.getStackInHand(Hand.MAIN_HAND);
            PersistentProjectileEntity persistentProjectileEntity = createArrowProjectile(skeleton, itemStack, pullProgress);

            double d = target.getX() - skeleton.getX();
            double e = target.getBodyY(TARGET_HEIGHT_FACTOR) - persistentProjectileEntity.getY();
            double f = target.getZ() - skeleton.getZ();

            float g = MathHelper.sqrt((float) (d * d + f * f)) * VELOCITY_SPREAD_FACTOR;

            persistentProjectileEntity.setVelocity(d, e + g, f, ARROW_VELOCITY, 0.0F);

            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * DAMAGE_MULTIPLIER);

            float soundPitch = 1.0F / (skeleton.getRandom().nextFloat() * 0.4F + 0.8F);
            skeleton.getWorld().playSound(null, skeleton.getX(), skeleton.getY(), skeleton.getZ(),
                    SoundEvents.ENTITY_SKELETON_SHOOT, SoundCategory.HOSTILE, 1.0F, soundPitch);

            skeleton.getWorld().spawnEntity(persistentProjectileEntity);
        }
    }

    @Unique
    private PersistentProjectileEntity createArrowProjectile(AbstractSkeletonEntity skeleton, ItemStack arrow, float pullProgress) {
        ArrowItem arrowItem = (ArrowItem)(arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(skeleton.getWorld(), arrow, skeleton);
        persistentProjectileEntity.setCritical(true);
        return persistentProjectileEntity;
    }
}