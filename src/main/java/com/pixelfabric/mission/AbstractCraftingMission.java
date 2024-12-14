package com.pixelfabric.mission;

import com.pixelfabric.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractCraftingMission implements Mission {
    private final Item targetItem;
    private final int requiredAmount;
    protected final Set<UUID> blueCoinGiven = new HashSet<>();

    public AbstractCraftingMission(Item targetItem, int requiredAmount) {
        this.targetItem = targetItem;
        this.requiredAmount = requiredAmount;
    }

    @Override
    public boolean checkCompletion(PlayerEntity player) {
        ItemStack blueCoinStack = null;
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty() && stack.isOf(ModItems.BLUECOIN)) {
                blueCoinStack = stack;
                break;
            }
        }

        if (blueCoinStack == null) {
            player.sendMessage(Text.literal("Necesitas una Deathcoin azul para reclamar esta recompensa.")
                    .formatted(Formatting.RED), false);
            return false;
        }

        int count = countItemInInventory(player, targetItem);
        if (count < requiredAmount) {
            player.sendMessage(Text.literal("Necesitas craftear " + requiredAmount + " " +
                            targetItem.getName().getString() + ". Tienes: " + count)
                    .formatted(Formatting.RED), false);
            return false;
        }

        // Solo quitamos la Deathcoin azul, no los items crafteados
        blueCoinStack.decrement(1);
        return true;
    }

    public boolean checkCraftingRequirement(ServerPlayerEntity player) {
        return countItemInInventory(player, targetItem) >= requiredAmount && !blueCoinGiven.contains(player.getUuid());
    }

    private int countItemInInventory(PlayerEntity player, Item item) {
        int count = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty() && stack.isOf(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public void checkAndReward(ServerPlayerEntity player) {
        if (checkCraftingRequirement(player)) {
            ItemStack blueCoin = new ItemStack(ModItems.BLUECOIN);
            player.getInventory().insertStack(blueCoin);
            blueCoinGiven.add(player.getUuid());

            player.playSound(
                    SoundEvents.ENTITY_PLAYER_LEVELUP,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
    }

    @Override
    public String getDescription() {
        return "Craftea " + requiredAmount + " " + targetItem.getName().getString();
    }
}