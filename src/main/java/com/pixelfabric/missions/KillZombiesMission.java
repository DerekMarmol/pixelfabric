package com.pixelfabric.missions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public class KillZombiesMission implements Mission {
    @Override
    public boolean checkCompletion(PlayerEntity player) {
        // Verificar si el jugador tiene el token de completación
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                NbtCompound nbt = stack.getNbt();
                if (nbt != null && "zombie_kills".equals(nbt.getString("MissionToken"))) {
                    // Remover el token cuando complete la misión
                    stack.setCount(0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void giveReward(PlayerEntity player) {
        // Recompensa: 5 diamantes, 10 botellas de experiencia y 5 manzanas doradas
        player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 5));
        player.getInventory().insertStack(new ItemStack(Items.EXPERIENCE_BOTTLE, 10));
        player.getInventory().insertStack(new ItemStack(Items.GOLDEN_APPLE, 5));
    }

    @Override
    public String getDescription() {
        return "Elimina 10 zombies y reclama tu recompensa";
    }
}