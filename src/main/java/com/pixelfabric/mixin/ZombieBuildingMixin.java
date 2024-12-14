package com.pixelfabric.mixin;

import com.pixelfabric.ai.ZombieBuildPathGoal;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public class ZombieBuildingMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addBuildingGoal(CallbackInfo ci) {
        ZombieEntity zombie = (ZombieEntity)(Object)this;
        ((MobEntityAccessor)zombie).getGoalSelector().add(2, new ZombieBuildPathGoal(zombie));
    }
}