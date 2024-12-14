package com.pixelfabric.mixin;

import com.pixelfabric.commands.SkeletonWitherMechanic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public class SkeletonAttackMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void modifySkeletonAttack(LivingEntity target, float pullProgress, CallbackInfo ci) {
        if (SkeletonWitherMechanic.isSkeletonWitherActive()) {
            AbstractSkeletonEntity skeleton = (AbstractSkeletonEntity) (Object) this;

            // Cancelamos el ataque original
            ci.cancel();

            // Calculamos la posición de disparo (desde la cabeza del esqueleto)
            double d = skeleton.getX();
            double e = skeleton.getY() + skeleton.getStandingEyeHeight();
            double f = skeleton.getZ();

            // Calculamos el vector hacia el objetivo
            double g = target.getX() - d;
            double h = target.getBodyY(0.5D) - e; // Apuntamos al centro del cuerpo
            double i = target.getZ() - f;

            // Creamos la calavera de wither usando la misma lógica que el Wither
            WitherSkullEntity witherSkullEntity = new WitherSkullEntity(
                    skeleton.getWorld(),
                    skeleton,
                    g,
                    h,
                    i
            );

            // Configuramos la posición inicial
            witherSkullEntity.setPos(d, e, f);

            // Configuramos propiedades adicionales
            witherSkullEntity.setOwner(skeleton);

            // Si quieres que algunas calaveras sean cargadas (más poderosas), puedes usar esto
            // La probabilidad es ajustable
            if (skeleton.getRandom().nextFloat() < 0.05f) { // 5% de probabilidad
                witherSkullEntity.setCharged(true);
            }

            // Reproducimos el sonido de disparo
            skeleton.getWorld().playSound(null, skeleton.getX(), skeleton.getY(), skeleton.getZ(),
                    SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 1.0f, 1.0f);

            // Spawneamos la calavera en el mundo
            skeleton.getWorld().spawnEntity(witherSkullEntity);
        }
    }
}