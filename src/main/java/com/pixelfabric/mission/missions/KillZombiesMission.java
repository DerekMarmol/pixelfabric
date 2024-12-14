package com.pixelfabric.mission.missions;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.mission.AbstractKillMission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class KillZombiesMission extends AbstractKillMission {
    public KillZombiesMission(int requiredKills) {
        super(requiredKills);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        player.getInventory().insertStack(new ItemStack(Items.GOLDEN_APPLE, 3));
        player.getInventory().insertStack(new ItemStack(Items.IRON_INGOT, 20));
        player.getInventory().insertStack(new ItemStack(ModItems.SILVERCOIN, 3));

        if (player.getRandom().nextFloat() < 0.3f) {
            player.getInventory().insertStack(new ItemStack(Items.EMERALD, 3));
        }
    }

    @Override
    public String getDescription() {
        return "Mata " + requiredKills + " zombies";
    }
}