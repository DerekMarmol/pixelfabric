package com.pixelfabric.mixin;

import com.pixelfabric.commands.TripleMobResistance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class TripleMobResistanceMixin {
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyDamage(float amount, DamageSource source) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        // Aplicar la resistencia triplicada
        float modifiedAmount = TripleMobResistance.modifyDamage(thisEntity, amount);

        return modifiedAmount;
    }
}