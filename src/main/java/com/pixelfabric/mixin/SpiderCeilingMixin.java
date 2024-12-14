package com.pixelfabric.mixin;

import com.pixelfabric.commands.SpiderCeilingMechanic;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpiderEntity.class)
public abstract class SpiderCeilingMixin {
    private int tickCounter = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (!SpiderCeilingMechanic.isSpiderCeilingActive()) return;

        SpiderEntity spider = (SpiderEntity) (Object) this;
        World world = spider.getWorld();

        // Verificar el bloque sólido cada 10 ticks
        if (tickCounter++ % 10 == 0) {
            BlockPos upPos = spider.getBlockPos().up(2);
            boolean isSolidAbove = world.getBlockState(upPos).isSolid();

            if (isSolidAbove) {
                // Solo cambiar gravedad si no está configurada ya
                if (!spider.hasNoGravity()) {
                    spider.setNoGravity(true);
                }
                // Forzar la posición para que parezca pegada al techo
                double targetY = upPos.getY() - spider.getHeight();
                if (spider.getY() < targetY) {
                    spider.setPos(spider.getX(), targetY, spider.getZ());
                }
            } else {
                // Restaurar la gravedad si no hay techo sólido
                if (spider.hasNoGravity()) {
                    spider.setNoGravity(false);
                }
            }
        }
    }
}
