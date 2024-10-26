package com.pixelfabric.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import com.pixelfabric.commands.NightmareMode;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "getLuminance", at = @At("RETURN"), cancellable = true)
    private void onGetLuminance(CallbackInfoReturnable<Integer> cir) {
        if (NightmareMode.isNightmareModeActive()) {
            int originalLuminance = cir.getReturnValue();
            int reducedLuminance = Math.max(0, originalLuminance / 2); // Reduce luminance by 50%
            cir.setReturnValue(reducedLuminance);
        }
    }
}
