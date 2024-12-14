package com.pixelfabric.effects.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.entity.damage.DamageSource;

public class ZombificationEffect extends StatusEffect {
    public ZombificationEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            DamageSource damageSource = new DamageSource(
                    player.getWorld().getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .entryOf(RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                                    new Identifier("minecraft", "magic")))
            ) {
                @Override
                public Text getDeathMessage(LivingEntity entity) {
                    return Text.translatable("death.attack.zombification", entity.getDisplayName());
                }
            };

            player.damage(damageSource, Float.MAX_VALUE);
        }
        super.onRemoved(entity, attributes, amplifier);
    }
}