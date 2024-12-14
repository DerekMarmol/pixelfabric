package com.pixelfabric.Enchantments.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResonanceEnchantment extends Enchantment {
    // Instancia estática para ser accedida desde el mixin
    public static ResonanceEnchantment INSTANCE;

    // Mapas para rastrear los contadores de golpes y tiempos de cada usuario
    private final Map<UUID, Integer> hitCounters = new HashMap<>();
    private final Map<UUID, Long> lastHitTimes = new HashMap<>();

    public int getHitCounter(UUID userId) {
        return hitCounters.getOrDefault(userId, 0);
    }

    public ResonanceEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        // Asignar la instancia estática
        INSTANCE = this;
    }

    /**
     * Obtiene el multiplicador de daño basado en el número de golpes consecutivos
     * @param user Entidad que realiza el ataque
     * @param level Nivel del encantamiento
     * @return Multiplicador de daño
     */
    public float getDamageMultiplier(LivingEntity user, int level) {
        UUID userId = user.getUuid();
        long currentTime = user.getWorld().getTime();
        long lastHit = lastHitTimes.getOrDefault(userId, 0L);

        // Reiniciar contador si ha pasado mucho tiempo entre golpes
        int hitCounter = hitCounters.getOrDefault(userId, 0);
        if (currentTime - lastHit > 40) { // Aproximadamente 2 segundos
            hitCounter = 0;
        }

        // Incrementar contador de golpes
        hitCounter++;
        hitCounters.put(userId, hitCounter);
        lastHitTimes.put(userId, currentTime);

        // Calcular aumento de daño según el contador de golpes
        float damageMultiplier;
        switch (hitCounter) {
            case 2:
                damageMultiplier = 1.60f; // 35% de aumento
                break;
            case 3:
                damageMultiplier = 1.70f; // 50% de aumento
                break;
            default:
                damageMultiplier = 1.0f; // Primer golpe, daño normal
        }

        // Si el contador llega a 3, reiniciarlo
        if (hitCounter >= 3) {
            hitCounters.put(userId, 0);
        }

        return damageMultiplier;
    }

    /**
     * Añade efectos visuales y de sonido cuando se aplica el encantamiento
     * @param user Entidad que realiza el ataque
     * @param target Entidad objetivo
     * @param damageMultiplier Multiplicador de daño actual
     */
    public void addResonanceEffects(LivingEntity user, Entity target, float damageMultiplier) {
        if (user.getWorld() instanceof ServerWorld serverWorld) {
            // Efecto de partículas
            serverWorld.spawnParticles(
                    ParticleTypes.ENCHANTED_HIT,
                    target.getX(),
                    target.getY() + target.getHeight() * 0.5,
                    target.getZ(),
                    (int)(5 * damageMultiplier), // Más partículas con más daño
                    0.2, 0.2, 0.2, 0.1
            );

            // Sonido de resonancia
            int hitCounter = hitCounters.getOrDefault(user.getUuid(), 0);
            serverWorld.playSound(
                    null,
                    target.getX(), target.getY(), target.getZ(),
                    SoundEvents.BLOCK_ANVIL_LAND, // Sonido de impacto metálico
                    SoundCategory.PLAYERS,
                    0.5f * (hitCounter), // Volumen aumenta con cada golpe
                    1.5f + (hitCounter * 0.2f) // Tono aumenta con cada golpe
            );
        }
    }

    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}