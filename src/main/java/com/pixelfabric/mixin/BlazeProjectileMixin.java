package com.pixelfabric.mixin;

import com.pixelfabric.commands.RiskyBlazeMechanic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class BlazeProjectileMixin {
    @Inject(method = "onEntityHit", at = @At("RETURN"))
    private void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        ProjectileEntity projectile = (ProjectileEntity) (Object) this;
        Entity owner = projectile.getOwner();
        Entity target = entityHitResult.getEntity();

        if (owner instanceof BlazeEntity && RiskyBlazeMechanic.isRiskyBlazeSwapActive()) {
            RiskyBlazeMechanic.swapPositions((BlazeEntity) owner, target);
        }
    }
}