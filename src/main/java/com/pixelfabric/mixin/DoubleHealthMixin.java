package com.pixelfabric.mixin;

import com.pixelfabric.commands.SpiderDoubleHealthMechanic;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpiderEntity.class)
public abstract class DoubleHealthMixin {
    @Inject(
            method = "initialize",
            at = @At("TAIL")
    )
    private void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                              EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        if (SpiderDoubleHealthMechanic.isSpiderDoubleHealthActive() && spawnReason != SpawnReason.CONVERSION) {
            SpiderEntity spider = (SpiderEntity) (Object) this;
            // Duplica la vida base de la araña
            EntityAttributeInstance healthAttribute = spider.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (healthAttribute != null) {
                double baseHealth = healthAttribute.getBaseValue();
                healthAttribute.setBaseValue(baseHealth * 2);
                // Cura la araña a su nueva vida máxima
                spider.setHealth(spider.getMaxHealth());
            }
        }
    }
}
