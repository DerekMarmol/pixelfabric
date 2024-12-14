package com.pixelfabric.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.UUID;

public class HeartTotemItem extends Item {
    // Usar un UUID diferente al de la armadura
    private static final UUID TOTEM_HEALTH_MODIFIER_ID = UUID.fromString("8D6F0BA2-1186-46AC-B896-C61C5CEE99DD");
    private static final String MODIFIER_NAME = "Heart Totem Health Bonus";
    private static final double HEALTH_BONUS = 2.0D; // 1 corazón = 2 puntos de vida

    public HeartTotemItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof PlayerEntity player)) {
            return;
        }

        boolean shouldHaveBonus = isTotemEquipped(player);
        var attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attribute == null) return;

        boolean hasModifier = attribute.getModifier(TOTEM_HEALTH_MODIFIER_ID) != null;

        if (shouldHaveBonus && !hasModifier) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    TOTEM_HEALTH_MODIFIER_ID,
                    MODIFIER_NAME,
                    HEALTH_BONUS,
                    EntityAttributeModifier.Operation.ADDITION
            );
            attribute.addPersistentModifier(modifier);
        } else if (!shouldHaveBonus && hasModifier) {
            attribute.removeModifier(TOTEM_HEALTH_MODIFIER_ID);
            adjustPlayerHealth(player);
        }
    }

    private boolean isTotemEquipped(PlayerEntity player) {
        return player.getMainHandStack().getItem() == this ||
                player.getOffHandStack().getItem() == this;
    }

    private void adjustPlayerHealth(PlayerEntity player) {
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    // Método útil para debug
    public static boolean hasTotemBonus(PlayerEntity player) {
        var attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        return attribute != null && attribute.getModifier(TOTEM_HEALTH_MODIFIER_ID) != null;
    }
}