package com.pixelfabric.missions;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EnchantedDiamondArmorMission implements Mission {
    @Override
    public boolean checkCompletion(PlayerEntity player) {
        return checkArmorPiece(player.getEquippedStack(EquipmentSlot.HEAD), Items.DIAMOND_HELMET) &&
                checkArmorPiece(player.getEquippedStack(EquipmentSlot.CHEST), Items.DIAMOND_CHESTPLATE) &&
                checkArmorPiece(player.getEquippedStack(EquipmentSlot.LEGS), Items.DIAMOND_LEGGINGS) &&
                checkArmorPiece(player.getEquippedStack(EquipmentSlot.FEET), Items.DIAMOND_BOOTS);
    }

    private boolean checkArmorPiece(ItemStack stack, net.minecraft.item.Item expectedItem) {
        if (!stack.isOf(expectedItem)) {
            return false;
        }

        // Verificar Protección II
        int protectionLevel = EnchantmentHelper.getLevel(Enchantments.PROTECTION, stack);
        if (protectionLevel < 2) {
            return false;
        }

        // Verificar Irrompibilidad
        int unbreakingLevel = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
        if (unbreakingLevel < 1) {
            return false;
        }

        return true;
    }

    @Override
    public void giveReward(PlayerEntity player) {
        // Recompensa: 10 diamantes y 3 libros de encantamiento
        player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 10));
        player.getInventory().insertStack(new ItemStack(Items.ENCHANTED_BOOK, 3));
    }

    @Override
    public String getDescription() {
        return "Equipa una armadura completa de diamante con Protección II e Irrompibilidad";
    }
}