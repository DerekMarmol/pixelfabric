package com.pixelfabric.mixin;

import com.pixelfabric.commands.ExplosiveSheepMechanic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class ExplosiveSheepMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        // Verificar que la entidad sea una oveja
        if (!((Object) this instanceof SheepEntity)) {
            return;
        }

        SheepEntity sheep = (SheepEntity) (Object) this;

        // Solo si la mecánica está activa
        if (!ExplosiveSheepMechanic.isExplosiveSheepActive()) {
            return;
        }

        // Solo en el servidor
        if (sheep.getWorld().isClient) {
            return;
        }

        // Verificar que fue matada por un jugador
        if (!(damageSource.getAttacker() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity killer = (PlayerEntity) damageSource.getAttacker();
        ServerWorld world = (ServerWorld) sheep.getWorld();

        // 25% de probabilidad de explotar
        if (sheep.getRandom().nextFloat() < 0.25f) {

            Vec3d explosionPos = sheep.getPos();

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
                        sheep.getBoundingBox().expand(15.0),
                        player -> !player.isSpectator() && !player.isCreative()
                );
            });
        }
    }
}
