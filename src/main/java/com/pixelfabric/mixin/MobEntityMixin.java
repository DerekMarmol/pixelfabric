package com.pixelfabric.mixin;

import com.pixelfabric.commands.NightPowerMode;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        MobEntity mob = (MobEntity) (Object) this;
        if (NightPowerMode.isNightPowerModeActive() && mob.getWorld().isNight()) {
            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20, 1, true, false));
            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 1, true, false));
        }
    }
}
