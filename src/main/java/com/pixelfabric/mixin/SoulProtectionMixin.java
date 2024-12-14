package com.pixelfabric.mixin;

import com.pixelfabric.effects.ModEffects;
import com.pixelfabric.effects.custom.SoulProtectionEffect;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class SoulProtectionMixin {
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (SoulProtectionEffect.hasSoulProtection(player)) {

            player.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(true, player.getServer());

            player.removeStatusEffect(ModEffects.SOUL_PROTECTION_EFFECT);

        }
    }
}
