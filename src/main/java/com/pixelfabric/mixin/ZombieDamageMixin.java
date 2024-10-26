package com.pixelfabric.mixin;

import com.pixelfabric.commands.ZombieEnhancementMechanic;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ZombieEntity.class)
public abstract class ZombieDamageMixin {
    @ModifyConstant(method = "createZombieAttributes", constant = @Constant(doubleValue = 3.0D))
    private static double modifyZombieDamage(double original) {
        return ZombieEnhancementMechanic.isZombieEnhancementActive() ? original * 3.0 : original;
    }
}