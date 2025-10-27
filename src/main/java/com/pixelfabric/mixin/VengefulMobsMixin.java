package com.pixelfabric.mixin;

import com.pixelfabric.commands.VengefulMobsMechanic;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class VengefulMobsMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onMobDeath(DamageSource damageSource, CallbackInfo ci) {
        // Solo si la mecánica está activa
        if (!VengefulMobsMechanic.isVengefulMobsActive()) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        // Solo aplicar a mobs hostiles (no jugadores, aldeanos, etc.)
        if (!(entity instanceof MobEntity)) {
            return;
        }

        MobEntity mob = (MobEntity) entity;
        World world = mob.getWorld();

        // Solo en el servidor
        if (world.isClient) {
            return;
        }

        // Verificar que fue matado por un jugador
        if (!(damageSource.getAttacker() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity killer = (PlayerEntity) damageSource.getAttacker();

        // 30% de probabilidad de que aparezca un mob vengativo
        if (mob.getRandom().nextFloat() < 0.3f) {

            ServerWorld serverWorld = (ServerWorld) world;
            EntityType<?> entityType = mob.getType();

            try {
                // Crear una nueva entidad del mismo tipo
                LivingEntity vengefulMob = (LivingEntity) entityType.create(serverWorld);

                if (vengefulMob != null) {
                    // Buscar una posición segura cerca del mob muerto
                    Vec3d spawnPos = findSafeSpawnPosition(serverWorld, mob.getPos(), 3.0, 8.0);

                    if (spawnPos != null) {
                        vengefulMob.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z,
                                mob.getRandom().nextFloat() * 360.0f, 0.0f);

                        // Si es un MobEntity, hacer que tenga como objetivo al jugador inmediatamente
                        if (vengefulMob instanceof MobEntity) {
                            MobEntity vengefulMobEntity = (MobEntity) vengefulMob;
                            vengefulMobEntity.setTarget(killer);
                        }

                        // Spawnar el mob
                        serverWorld.spawnEntity(vengefulMob);

                        // Efectos visuales y sonoros
                        serverWorld.playSound(
                                null,
                                spawnPos.x,
                                spawnPos.y,
                                spawnPos.z,
                                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                                SoundCategory.HOSTILE,
                                1.0f,
                                0.8f
                        );

                        // Encontrar jugadores cercanos para notificar
                        List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                                PlayerEntity.class,
                                vengefulMob.getBoundingBox().expand(20.0),
                                player -> !player.isSpectator() && !player.isCreative() && player != killer
                        );

                        for (PlayerEntity player : nearbyPlayers) {
                            player.sendMessage(
                                    Text.literal("§4¡Se siente una presencia vengativa cerca!"),
                                    true
                            );
                        }
                    }
                }

            } catch (Exception e) {
                // Si hay algún error al crear la entidad, no hacer nada
                System.err.println("Error creating vengeful mob: " + e.getMessage());
            }
        }
    }

    private Vec3d findSafeSpawnPosition(ServerWorld world, Vec3d centerPos, double minRadius, double maxRadius) {
        for (int attempts = 0; attempts < 10; attempts++) {
            double angle = world.getRandom().nextDouble() * 2 * Math.PI;
            double distance = minRadius + world.getRandom().nextDouble() * (maxRadius - minRadius);

            double x = centerPos.x + Math.cos(angle) * distance;
            double z = centerPos.z + Math.sin(angle) * distance;

            // Buscar una posición Y segura
            for (int y = (int) centerPos.y + 5; y >= (int) centerPos.y - 5; y--) {
                if (world.getBlockState(new net.minecraft.util.math.BlockPos((int) x, y, (int) z)).isAir() &&
                        world.getBlockState(new net.minecraft.util.math.BlockPos((int) x, y + 1, (int) z)).isAir() &&
                        !world.getBlockState(new net.minecraft.util.math.BlockPos((int) x, y - 1, (int) z)).isAir()) {

                    return new Vec3d(x, y, z);
                }
            }
        }

        // Si no encuentra una posición segura, usar la posición original pero un poco más arriba
        return centerPos.add(0, 1, 0);
    }
}