package com.pixelfabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.SpawnHelper;

@Mixin(SpawnHelper.class)
public interface SpawnHelperAccessor {
    @Accessor("CHUNK_AREA")
    static int getChunkArea() {
        throw new AssertionError();
    }
}
