package com.pixelfabric.datagen;

import com.pixelfabric.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;


import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR).
                add(ModItems.WITHER_HELMET, ModItems.WITHER_CHESTPLATE,ModItems.WITHER_LEGGINS,ModItems.WITHER_BOOTS);

        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR).
                add(ModItems.SUMMERTIME_HELMET, ModItems.SUMMERTIME_CHESTPLATE,ModItems.SUMMERTIME_LEGGINS, ModItems.SUMMERTIME_BOOTS);

    }
}
