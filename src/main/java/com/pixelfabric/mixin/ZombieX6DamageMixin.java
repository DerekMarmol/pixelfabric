package com.pixelfabric.mixin;

import com.pixelfabric.commands.ZombieX6Enhancement;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ZombieEntity.class)
public abstract class ZombieX6DamageMixin {
    private static final UUID DAMAGE_X6_MODIFIER_UUID = UUID.randomUUID();

    @Inject(method = "initGoals", at = @At("HEAD"))
    private void modifyZombieDamageX6(CallbackInfo ci) {
        if (ZombieX6Enhancement.isZombieX6Active()) {
            EntityAttributeInstance attribute = ((ZombieEntity) (Object) this).getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attribute != null) {
                attribute.addPersistentModifier(new EntityAttributeModifier(DAMAGE_X6_MODIFIER_UUID, "Zombie damage x6 boost", 6.0, EntityAttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
    }
}