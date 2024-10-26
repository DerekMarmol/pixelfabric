package com.pixelfabric.mixin;

import com.pixelfabric.commands.ZombieEnhancementMechanic;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieSunBurnMixin {
    @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
    private void preventSunSensitivity(CallbackInfoReturnable<Boolean> cir) {
        if (ZombieEnhancementMechanic.isZombieEnhancementActive()) {
            cir.setReturnValue(false);
        }
    }
}