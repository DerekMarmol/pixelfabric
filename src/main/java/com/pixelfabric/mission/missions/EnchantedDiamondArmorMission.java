package com.pixelfabric.mission.missions;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.mission.Mission;
import com.pixelfabric.mission.MissionManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EnchantedDiamondArmorMission implements Mission {
    @Override
    public boolean checkCompletion(PlayerEntity player) {
        // Verificar si tiene la BlueCoin
        ItemStack blueCoinStack = null;

        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty() && stack.isOf(ModItems.BLUECOIN)) {
                blueCoinStack = stack;
                break;
            }
        }

        if (blueCoinStack != null) {
            // Consumir la BlueCoin y permitir la recompensa
            blueCoinStack.decrement(1);
            return true;
        } else {
            player.sendMessage(Text.literal("Necesitas una BlueCoins para reclamar esta recompensa.")
                    .formatted(Formatting.RED), false);
            return false;
        }
    }

    public boolean checkArmorPieces(PlayerEntity player) {
        return checkArmorPiece(player.getEquippedStack(EquipmentSlot.HEAD), Items.DIAMOND_HELMET) &&
                checkArmorPiece(player.getEquippedStack(EquipmentSlot.CHEST), Items.DIAMOND_CHESTPLATE) &&
                checkArmorPiece(player.getEquippedStack(EquipmentSlot.LEGS), Items.DIAMOND_LEGGINGS) &&
                checkArmorPiece(player.getEquippedStack(EquipmentSlot.FEET), Items.DIAMOND_BOOTS);
    }

    private boolean checkArmorPiece(ItemStack stack, Item expectedItem) {
        if (!stack.isOf(expectedItem)) {
            return false;
        }

        int protectionLevel = EnchantmentHelper.getLevel(Enchantments.PROTECTION, stack);
        if (protectionLevel < 3) {
            return false;
        }

        int unbreakingLevel = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
        if (unbreakingLevel < 2) {
            return false;
        }

        return true;
    }

    @Override
    public void giveReward(PlayerEntity player) {
        player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 3));
        player.getInventory().insertStack(new ItemStack(Items.ENCHANTED_BOOK, 1));
        player.getInventory().insertStack(new ItemStack(ModItems.SILVERCOIN, 3));

        if (player.getRandom().nextFloat() < 0.5f) {
            player.getInventory().insertStack(new ItemStack(Items.ENCHANTED_BOOK, 1));
        }
    }

    @Override
    public String getDescription() {
        return "Equipa una armadura completa de diamante con Protección II e Irrompibilidad";
    }

    public void checkAndReward(ServerPlayerEntity player) {
        if (checkArmorPieces(player) && !MissionManager.getInstance().hasCompletedMission(player.getUuid())) {
            ItemStack blueCoin = new ItemStack(ModItems.BLUECOIN);
            player.getInventory().insertStack(blueCoin);

            player.getServer().getPlayerManager();
            player.playSound(
                    SoundEvents.ENTITY_PLAYER_LEVELUP,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
    }
}