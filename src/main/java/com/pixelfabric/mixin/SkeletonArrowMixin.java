package com.pixelfabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import com.pixelfabric.commands.SkeletonEnhancementMechanic;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class SkeletonArrowMixin {
    @Inject(
            method = "onEntityHit",
            at = @At("HEAD")
    )
    private void onArrowHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity arrow = (PersistentProjectileEntity) (Object) this;
        Entity owner = arrow.getOwner();
        Entity hitEntity = entityHitResult.getEntity();

        if (owner instanceof SkeletonEntity && hitEntity instanceof PlayerEntity &&
                SkeletonEnhancementMechanic.isSkeletonEnhancementActive()) {

            PlayerEntity player = (PlayerEntity) hitEntity;

            if (!player.isCreative()) {
                double skeletonX = owner.getX();
                double skeletonY = owner.getY();
                double skeletonZ = owner.getZ();

                if (owner.getWorld() instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) owner.getWorld();
                    serverWorld.spawnParticles(
                            ParticleTypes.PORTAL,
                            player.getX(),
                            player.getY() + 0.5,
                            player.getZ(),
                            50,
                            0.5,
                            0.5,
                            0.5,
                            0.1
                    );
                }


                player.teleport(
                        skeletonX,
                        skeletonY,
                        skeletonZ
                );

                player.getWorld().playSound(
                        null,
                        skeletonX,
                        skeletonY,
                        skeletonZ,
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                        SoundCategory.HOSTILE,
                        1.0F,
                        1.0F
                );

                if (owner.getWorld() instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) owner.getWorld();
                    serverWorld.spawnParticles(
                            ParticleTypes.PORTAL,
                            skeletonX,
                            skeletonY + 0.5,
                            skeletonZ,
                            50,
                            0.5,
                            0.5,
                            0.5,
                            0.1
                    );
                }
            }
        }
    }
}
