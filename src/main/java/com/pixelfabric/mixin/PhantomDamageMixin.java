package com.pixelfabric.mixin;

import com.pixelfabric.commands.PhantomEnhancementMechanic;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PhantomEntity.class)
public abstract class PhantomDamageMixin {
    private static final UUID DAMAGE_MODIFIER_UUID = UUID.randomUUID();

    @Inject(method = "initGoals", at = @At("HEAD"))
    private void modifyPhantomDamage(CallbackInfo ci) {
        if (PhantomEnhancementMechanic.isPhantomEnhancementActive()) {
            EntityAttributeInstance attribute = ((PhantomEntity) (Object) this).getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attribute != null) {
                attribute.addPersistentModifier(new EntityAttributeModifier(DAMAGE_MODIFIER_UUID, "Phantom damage boost", 3.0, EntityAttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
    }
}