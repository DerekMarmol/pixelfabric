package com.pixelfabric.utils;

import com.pixelfabric.effects.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class DamageUtils {

    public static boolean shouldBlockDamage(LivingEntity entity, DamageSource source) {
        // Verifica si la entidad tiene el efecto de inmunidad activo
        return entity.hasStatusEffect(ModEffects.INMUNITY_EFFECT);
    }
}
