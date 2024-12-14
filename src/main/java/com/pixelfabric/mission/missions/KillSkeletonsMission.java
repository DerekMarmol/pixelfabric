package com.pixelfabric.mission.missions;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.mission.AbstractKillMission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class KillSkeletonsMission extends AbstractKillMission {

    public KillSkeletonsMission(int requiredKills) {
        super(requiredKills);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        // Recompensas más balanceadas
        player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 3));
        player.getInventory().insertStack(new ItemStack(Items.GOLDEN_APPLE, 3));
        player.getInventory().insertStack(new ItemStack(ModItems.SILVERCOIN, 3));

        if (player.getRandom().nextFloat() < 0.5f) {
            player.getInventory().insertStack(new ItemStack(Items.BOW, 1));
        }
    }

    @Override
    public String getDescription() {
        return "Elimina " + requiredKills + " esqueletos y reclama tu recompensa";
    }
}