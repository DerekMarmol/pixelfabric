package com.pixelfabric.mission.missions;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.mission.AbstractCraftingMission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BackpackCraftingMission extends AbstractCraftingMission {
    public BackpackCraftingMission() {
        super(ModItems.BACKPACK, 1);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 5));
        player.getInventory().insertStack(new ItemStack(ModItems.SILVERCOIN, 3));
        player.getInventory().insertStack(new ItemStack(Items.LEATHER, 10));

        // 40% de probabilidad de obtener una mochila extra
        if (player.getRandom().nextFloat() < 0.4f) {
            player.getInventory().insertStack(new ItemStack(ModItems.BACKPACK));
        }
    }
}