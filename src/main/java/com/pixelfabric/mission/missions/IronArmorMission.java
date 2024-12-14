package com.pixelfabric.mission.missions;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.mission.Mission;
import com.pixelfabric.mission.MissionManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class IronArmorMission implements Mission {
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
            player.sendMessage(Text.literal("Necesitas una Deathcoin azul para reclamar esta recompensa.")
                    .formatted(Formatting.RED), false);
            return false;
        }
    }

    public boolean checkArmorPieces(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.IRON_HELMET) &&
                player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.IRON_CHESTPLATE) &&
                player.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.IRON_LEGGINGS) &&
                player.getEquippedStack(EquipmentSlot.FEET).isOf(Items.IRON_BOOTS);
    }

    @Override
    public void giveReward(PlayerEntity player) {
        // Recompensa más balanceada
        player.getInventory().insertStack(new ItemStack(ModItems.SUMMERTIME_BOOTS, 1));
        player.getInventory().insertStack(new ItemStack(ModItems.SUMMERTIME_HELMET, 1 ));
        player.getInventory().insertStack(new ItemStack(ModItems.SILVERCOIN, 3));

        // 30% de probabilidad de un diamante extra
        if (player.getRandom().nextFloat() < 0.3f) {
            player.getInventory().insertStack(new ItemStack(Items.DIAMOND, 1));
        }
    }

    @Override
    public String getDescription() {
        return "Equipa una armadura completa de hierro";
    }

    // Método para verificar cuando el jugador equipa armadura
    public void checkAndReward(ServerPlayerEntity player) {
        if (checkArmorPieces(player) && !MissionManager.getInstance().hasCompletedMission(player.getUuid())) {
            // Dar BlueCoin
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

