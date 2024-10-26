package com.pixelfabric.mixin;

import com.pixelfabric.commands.NightmareMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkLightProvider.class)
public class ChunkLightProviderMixin {
    @Inject(method = "getLightLevel", at = @At("RETURN"), cancellable = true)
    private void onGetLightLevel(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (NightmareMode.isNightmareModeActive()) {
            int originalLight = cir.getReturnValue();
            int reducedLight = Math.max(0, originalLight - 7); // Reduce light level by ~50%
            cir.setReturnValue(reducedLight);
        }
    }
}
