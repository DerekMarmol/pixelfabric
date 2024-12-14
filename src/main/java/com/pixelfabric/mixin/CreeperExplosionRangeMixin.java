package com.pixelfabric.mixin;

import com.pixelfabric.commands.CreeperExplosionMechanic;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class CreeperExplosionRangeMixin {
    @Shadow
    private int explosionRadius;

    @Inject(method = "explode", at = @At("HEAD"), cancellable = false)
    private void modifyCreeperExplosion(CallbackInfo ci) {
        if (CreeperExplosionMechanic.isCreeperExplosionRangeActive()) {
            // Aumentar el radio de explosión en un 25%
            this.explosionRadius = (int) (this.explosionRadius * 1.70);
        }
    }
}