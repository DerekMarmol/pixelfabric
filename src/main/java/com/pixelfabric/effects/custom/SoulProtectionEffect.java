package com.pixelfabric.effects.custom;

import com.pixelfabric.effects.ModEffects;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.LivingEntity;

public class SoulProtectionEffect extends StatusEffect {
    public SoulProtectionEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static boolean hasSoulProtection(LivingEntity entity) {
        return entity.hasStatusEffect(ModEffects.SOUL_PROTECTION_EFFECT);
    }
}
