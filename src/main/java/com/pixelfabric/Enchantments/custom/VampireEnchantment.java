package com.pixelfabric.Enchantments.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VampireEnchantment extends Enchantment {
    private final Map<UUID, Integer> hitCounter = new HashMap<>();
    private final Map<UUID, Long> lastHitTime = new HashMap<>();

    public VampireEnchantment() {
        super(Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity && user != null) {
            UUID userId = user.getUuid();
            long currentTime = user.getWorld().getTime();

            long lastHit = lastHitTime.getOrDefault(userId, 0L);
            if (currentTime - lastHit > 10) {

                int currentHits = hitCounter.getOrDefault(userId, 0) + 1;

                lastHitTime.put(userId, currentTime);

                if (currentHits >= 2) {
                    user.heal(1.0f);

                    if (user.getWorld() instanceof ServerWorld) {
                        ServerWorld serverWorld = (ServerWorld) user.getWorld();
                        double x = user.getX();
                        double y = user.getY();
                        double z = user.getZ();

                        for (int i = 0; i < 8; i++) {
                            double offsetX = user.getRandom().nextDouble() * 0.5 - 0.25;
                            double offsetY = user.getRandom().nextDouble() * 0.5 - 0.25;
                            double offsetZ = user.getRandom().nextDouble() * 0.5 - 0.25;

                            serverWorld.spawnParticles(ParticleTypes.HEART,
                                    x + offsetX,
                                    y + 1 + offsetY,
                                    z + offsetZ,
                                    1, 0, 0, 0, 0);
                        }

                        serverWorld.playSound(null,
                                x, y, z,
                                SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH,
                                SoundCategory.PLAYERS,
                                0.5f,
                                1.0f);
                    }

                    currentHits = 0;
                }

                hitCounter.put(userId, currentHits);
            }
        }

        super.onTargetDamaged(user, target, level);
    }
}
