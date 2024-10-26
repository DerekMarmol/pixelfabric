package com.pixelfabric.missions;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class DiamondArmorMission implements Mission {
    @Override
    public boolean checkCompletion(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DIAMOND_HELMET) &&
                player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.DIAMOND_CHESTPLATE) &&
                player.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.DIAMOND_LEGGINGS) &&
                player.getEquippedStack(EquipmentSlot.FEET).isOf(Items.DIAMOND_BOOTS);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 5));
    }

    @Override
    public String getDescription() {
        return "Equipa una armadura completa de diamante";
    }
}
