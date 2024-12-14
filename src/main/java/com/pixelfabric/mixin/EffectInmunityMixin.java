package com.pixelfabric.mixin;

import com.pixelfabric.utils.DamageUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import com.pixelfabric.effects.ModEffects;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class EffectInmunityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void preventDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (DamageUtils.shouldBlockDamage(entity, source)) {
            cir.setReturnValue(false); // Cancela el daño
        }
    }
}
