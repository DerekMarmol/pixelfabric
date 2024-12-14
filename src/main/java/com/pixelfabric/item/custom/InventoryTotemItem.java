package com.pixelfabric.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryTotemItem extends Item {
    public InventoryTotemItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false; // Efecto de encantamiento
    }
}