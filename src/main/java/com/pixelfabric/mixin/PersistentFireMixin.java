package com.pixelfabric.mixin;

import com.pixelfabric.commands.PersistentFireMechanic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class PersistentFireMixin {
    @Inject(method = "baseTick", at = @At("TAIL"))
    private void modifyFireTick(CallbackInfo ci) {
        if (PersistentFireMechanic.isPersistentFireActive()) {
            LivingEntity entity = (LivingEntity) (Object) this;

            // Si la entidad está en fuego
            if (entity.isOnFire()) {
                // Si está en agua, apagar el fuego
                if (entity.isWet()) {
                    entity.setOnFireFor(0);
                }
                // Si no, mantener el fuego activo
                else {
                    // Restaurar el tiempo de fuego si no está en agua
                    entity.setOnFireFor(2); // Mantiene el fuego activo
                }
            }
        }
    }
}