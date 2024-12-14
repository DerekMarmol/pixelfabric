package com.pixelfabric.mixin;

import com.pixelfabric.commands.SpiderAggressionMechanic;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SpiderEntity.class)
public abstract class SpiderAggressionMixin {
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void modifySpiderAggression(CallbackInfo ci) {
        SpiderEntity spider = (SpiderEntity)(Object)this;

        // Verificar si la mecánica está activada
        if (SpiderAggressionMechanic.isSpiderAggressionActive()) {
            // Verificar que no esté montando a otro mob
            if (!spider.hasPassengers()) {
                // Buscar jugadores cercanos
                List<PlayerEntity> nearbyPlayers = spider.getWorld().getEntitiesByClass(
                        PlayerEntity.class,
                        spider.getBoundingBox().expand(16.0), // Radio de detección de 16 bloques
                        player -> !player.isCreative() && !player.isSpectator()
                );

                // Si hay jugadores cerca, establecer como objetivo
                if (!nearbyPlayers.isEmpty()) {
                    spider.setTarget(nearbyPlayers.get(0));
                }
            }
        }
    }
}
