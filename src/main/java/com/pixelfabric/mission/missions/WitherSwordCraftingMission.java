package com.pixelfabric.mission.missions;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.mission.AbstractCraftingMission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class WitherSwordCraftingMission extends AbstractCraftingMission {
    public WitherSwordCraftingMission() {
        super(ModItems.WITHER_SWORD, 1);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        // Puedes personalizar la recompensa como prefieras
        player.getInventory().insertStack(new ItemStack(Items.NETHERITE_INGOT, 2));
        player.getInventory().insertStack(new ItemStack(ModItems.SILVERCOIN, 5));
        player.getInventory().insertStack(new ItemStack(Items.GOLDEN_APPLE, 5));
        player.getInventory().insertStack(new ItemStack(ModItems.HEART_CONTAINER, 1));
        player.getInventory().insertStack(new ItemStack(ModItems.INVENTORY_TOTEM,1));

        // 30% de probabilidad de obtener un item extra
        if (player.getRandom().nextFloat() < 0.3f) {
            player.getInventory().insertStack(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
        }
    }
}