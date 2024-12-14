package com.pixelfabric.Enchantments.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BleedEnchantment extends Enchantment {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BleedEnchantment() {
        super(Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 10;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity) {
            LivingEntity targetEntity = (LivingEntity) target;

            // Aplicar daño 3 veces en intervalos de 0.5 segundos
            for (int i = 1; i <= 3; i++) {
                scheduler.schedule(() -> {
                    if (targetEntity.isAlive()) {
                        targetEntity.damage(user.getDamageSources().magic(), 0.5f); // 0.5 de daño (medio corazón)

                        // Efecto visual de partículas de daño
                        if (targetEntity.getWorld() instanceof ServerWorld) {
                            ServerWorld serverWorld = (ServerWorld) targetEntity.getWorld();
                            serverWorld.spawnParticles(
                                    ParticleTypes.DAMAGE_INDICATOR,
                                    targetEntity.getX(),
                                    targetEntity.getY() + targetEntity.getHeight() * 0.5,
                                    targetEntity.getZ(),
                                    5, 0.2, 0.2, 0.2, 0.1);
                        }
                    }
                }, i * 500, TimeUnit.MILLISECONDS);
            }
        }

        super.onTargetDamaged(user, target, level);
    }
}
