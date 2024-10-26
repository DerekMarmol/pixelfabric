package com.pixelfabric.mixin;

import com.pixelfabric.commands.BetrayalMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;

        if (thisEntity instanceof PassiveEntity && BetrayalMode.isBetrayalModeActive()) {
            World world = thisEntity.getWorld();

            if (!world.isClient) {
                for (Entity entity : world.getOtherEntities(thisEntity, thisEntity.getBoundingBox().expand(1.0))) {
                    if (entity instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity) entity;
                        player.damage(thisEntity.getDamageSources().mobAttack((PassiveEntity)thisEntity), 1.0f);
                        break;
                    }
                }
            }
        }
    }
}
