package com.pixelfabric.missions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public class KillSkeletonsMission implements Mission {
    @Override
    public boolean checkCompletion(PlayerEntity player) {
        // Verificar si el jugador tiene el token de completación
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                NbtCompound nbt = stack.getNbt();
                if (nbt != null && "skeleton_kills".equals(nbt.getString("MissionToken"))) {
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
        // Recompensa: 3 diamantes, 32 flechas y 3 arcos
        player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 3));
        player.getInventory().insertStack(new ItemStack(Items.ARROW, 32));
        player.getInventory().insertStack(new ItemStack(Items.BOW, 1));
    }

    @Override
    public String getDescription() {
        return "Elimina 10 esqueletos y reclama tu recompensa";
    }
}