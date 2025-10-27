package com.pixelfabric.mixin;

import com.pixelfabric.commands.ExplosiveAllMobsMechanic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
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
public abstract class ExplosiveAllMobsMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathAllMobs(DamageSource damageSource, CallbackInfo ci) {
        // Verificar que sea un mob (animal o mob hostil)
        if (!((Object) this instanceof AnimalEntity) && !((Object) this instanceof MobEntity)) {
            return;
        }

        // Excluir jugadores
        if ((Object) this instanceof PlayerEntity) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        // Solo si la mecánica está activa
        if (!ExplosiveAllMobsMechanic.isExplosiveAllMobsActive()) {
            return;
        }

        // Solo en el servidor
        if (entity.getWorld().isClient) {
            return;
        }

        // Verificar que fue matada por un jugador
        if (!(damageSource.getAttacker() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity killer = (PlayerEntity) damageSource.getAttacker();
        ServerWorld world = (ServerWorld) entity.getWorld();

        // 25% de probabilidad de explotar
        if (entity.getRandom().nextFloat() < 0.25f) {

            Vec3d explosionPos = entity.getPos();

            // Programar la explosión para el siguiente tick
            world.getServer().execute(() -> {
                // Explosión variable según el tamaño del mob
                float explosionPower = 2.0f;
                if (entity instanceof MobEntity) {
                    explosionPower = 2.5f; // Mobs hostiles explotan más fuerte
                }

                world.createExplosion(
                        null,
                        explosionPos.x,
                        explosionPos.y,
                        explosionPos.z,
                        explosionPower,
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
                        1.3f
                );

                List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                        PlayerEntity.class,
                        entity.getBoundingBox().expand(15.0),
                        player -> !player.isSpectator() && !player.isCreative()
                );
            });
        }
    }
}