package com.pixelfabric.mixin;

import com.pixelfabric.commands.FallDamageMechanic;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class FallDamageMixin {

    @Inject(
            method = "computeFallDamage",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onComputeFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        if (FallDamageMechanic.isFallDamageModifierActive()) {
            // Obtiene el daño original y lo aumenta en 25%
            int originalDamage = cir.getReturnValue();
            int modifiedDamage = (int)(originalDamage * 1.75f);
            cir.setReturnValue(modifiedDamage);
        }
    }
}