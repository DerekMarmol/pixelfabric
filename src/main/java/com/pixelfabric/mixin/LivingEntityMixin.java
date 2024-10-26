package com.pixelfabric.mixin;


import com.pixelfabric.commands.BetrayalMode;
import com.pixelfabric.commands.DoubleMobDamage;
import com.pixelfabric.commands.EnhancedMobResistance;
import com.pixelfabric.commands.UnreliableTotem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyDamage(float amount, DamageSource source) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        float modifiedAmount = DoubleMobDamage.modifyDamage(thisEntity, source, amount);

        if (BetrayalMode.isBetrayalModeActive() && source.getAttacker() instanceof PassiveEntity) {
            if (thisEntity instanceof PlayerEntity) {
                modifiedAmount += 2.0f;
            }
        }

        modifiedAmount = EnhancedMobResistance.modifyIncomingDamage(thisEntity, modifiedAmount);

        return modifiedAmount;
    }

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void onTryUseTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!UnreliableTotem.shouldTotemWork()) {
            cir.setReturnValue(false); // El tótem falla
            if ((Object)this instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)(Object)this).sendMessage(Text.literal("¡Tu tótem ha fallado!"), false);
            }
        }
    }
}

