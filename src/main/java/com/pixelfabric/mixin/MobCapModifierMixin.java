package com.pixelfabric.mixin;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.SpawnHelper;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.pixelfabric.config.MobCapConfig;

@Mixin(SpawnHelper.Info.class)
public class MobCapModifierMixin {
    private static final SpawnGroup TARGET_GROUP = SpawnGroup.MONSTER;
    private static final int MAX_CAP = 100;

    @Inject(method = "isBelowCap", at = @At("HEAD"), cancellable = true)
    private void modifyMobCap(SpawnGroup group, ChunkPos chunkPos, CallbackInfoReturnable<Boolean> cir) {
        if (group != TARGET_GROUP || !isModificationNeeded()) {
            return;
        }

        SpawnHelper.Info info = (SpawnHelper.Info) (Object) this;
        float multiplier = MobCapConfig.getMobCapMultiplier();

        int currentCount = getCurrentMobCount(info, group);
        int modifiedCap = calculateModifiedCap(info, group, multiplier);

        cir.setReturnValue(currentCount < modifiedCap);
    }

    private static boolean isModificationNeeded() {
        float multiplier = MobCapConfig.getMobCapMultiplier();
        return multiplier != 1.0f;
    }

    private static int getCurrentMobCount(SpawnHelper.Info info, SpawnGroup group) {
        return info.getGroupToCount().getOrDefault(group, 0);
    }

    private static int calculateModifiedCap(SpawnHelper.Info info, SpawnGroup group, float multiplier) {
        int baseCapacity = group.getCapacity();
        int spawnChunks = info.getSpawningChunkCount();
        int chunkArea = SpawnHelperAccessor.getChunkArea();

        int originalCap = (baseCapacity * spawnChunks) / chunkArea;
        int modifiedCap = Math.round(originalCap * multiplier);

        return Math.min(modifiedCap, MAX_CAP);
    }
}