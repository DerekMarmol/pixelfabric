// Archivo: mixin/InvertedControlsMixin.java
package com.pixelfabric.mixin;

import com.pixelfabric.effects.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class InvertedControlsMixin {

    private boolean skipNextTravel = false;

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        if (skipNextTravel) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity player)) return;

        if (player.hasStatusEffect(ModEffects.INVERTED_CONTROLS_EFFECT)) {
            Vec3d invertedInput = new Vec3d(-movementInput.x, movementInput.y, -movementInput.z);

            skipNextTravel = true;
            entity.travel(invertedInput);
            skipNextTravel = false;

            ci.cancel();
        }
    }
}

