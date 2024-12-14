package com.pixelfabric.item.custom.effects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.UUID;

public class WitherArmorEffect implements ArmorEffect {
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC");

    @Override
    public void applyEffect(PlayerEntity player) {
        if (player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .getModifier(HEALTH_MODIFIER_UUID) == null) {
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                    .addTemporaryModifier(new EntityAttributeModifier(HEALTH_MODIFIER_UUID,
                            "Health modifier", 4.0, EntityAttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public void removeEffect(PlayerEntity player) {
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .removeModifier(HEALTH_MODIFIER_UUID);
    }
}
