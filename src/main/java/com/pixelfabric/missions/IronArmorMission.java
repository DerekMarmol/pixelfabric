package com.pixelfabric.missions;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class IronArmorMission implements Mission{
    @Override
    public boolean checkCompletion(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.IRON_HELMET) &&
                player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.IRON_CHESTPLATE) &&
                player.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.IRON_LEGGINGS) &&
                player.getEquippedStack(EquipmentSlot.FEET).isOf(Items.IRON_BOOTS);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 5));
    }

    @Override
    public String getDescription() {
        return "Hazte de una armadura completa de hierro.";
    }
}

