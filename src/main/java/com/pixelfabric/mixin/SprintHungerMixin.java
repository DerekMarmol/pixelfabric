package com.pixelfabric.mixin;

import com.pixelfabric.commands.SprintHungerMechanic;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class SprintHungerMixin {
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.isSprinting() && !player.isCreative() && !player.isSpectator() && SprintHungerMechanic.isSprintHungerActive()) {
            player.getHungerManager().addExhaustion(0.1f);
        }
    }
}