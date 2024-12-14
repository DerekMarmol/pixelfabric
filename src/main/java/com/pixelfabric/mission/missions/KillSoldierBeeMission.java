package com.pixelfabric.mission.missions;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.mission.AbstractKillMission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class KillSoldierBeeMission extends AbstractKillMission {
    public KillSoldierBeeMission(int requiredKills) {
        super(requiredKills);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        player.getInventory().insertStack(new ItemStack(Items.HONEY_BLOCK, 3));
        player.getInventory().insertStack(new ItemStack(Items.HONEYCOMB, 5));
        player.getInventory().insertStack(new ItemStack(ModItems.SILVERCOIN, 3));

        if (player.getRandom().nextFloat() < 0.4f) {
            player.getInventory().insertStack(new ItemStack(Items.BEE_SPAWN_EGG, 1));
        }
    }

    @Override
    public String getDescription() {
        return "Elimina " + requiredKills + " Soldier Bees y reclama tu recompensa";
    }
}