package com.pixelfabric.mixin;

import com.pixelfabric.commands.ExplosivePigMechanic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class ExplosivePigMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathPig(DamageSource damageSource, CallbackInfo ci) {
        // Verificar que la entidad sea un cerdo
        if (!((Object) this instanceof PigEntity)) {
            return;
        }

        PigEntity pig = (PigEntity) (Object) this;

        // Solo si la mecánica está activa
        if (!ExplosivePigMechanic.isExplosivePigActive()) {
            return;
        }

        // Solo en el servidor
        if (pig.getWorld().isClient) {
            return;
        }

        // Verificar que fue matada por un jugador
        if (!(damageSource.getAttacker() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity killer = (PlayerEntity) damageSource.getAttacker();
        ServerWorld world = (ServerWorld) pig.getWorld();

        // 25% de probabilidad de explotar
        if (pig.getRandom().nextFloat() < 0.25f) {

            Vec3d explosionPos = pig.getPos();

            // Programar la explosión para el siguiente tick
            world.getServer().execute(() -> {
                world.createExplosion(
                        null,
                        explosionPos.x,
                        explosionPos.y,
                        explosionPos.z,
                        2.0f,
                        false,
                        ServerWorld.ExplosionSourceType.MOB
                );

                world.playSound(
                        null,
                        explosionPos.x,
                        explosionPos.y,
                        explosionPos.z,
                        SoundEvents.ENTITY_TNT_PRIMED,
                        SoundCategory.HOSTILE,
                        1.0f,
                        1.5f
                );

                List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                        PlayerEntity.class,
                        pig.getBoundingBox().expand(15.0),
                        player -> !player.isSpectator() && !player.isCreative()
                );
            });
        }
    }
}