package com.pixelfabric.datagen;

import com.eliotlash.mclib.math.functions.classic.Mod;
import com.pixelfabric.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.WITHER_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.WITHER_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.WITHER_LEGGINS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.WITHER_BOOTS));

        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SUMMERTIME_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SUMMERTIME_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SUMMERTIME_LEGGINS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SUMMERTIME_BOOTS));

        itemModelGenerator.register(ModItems.WITHER_SWORD, Models.HANDHELD);

        itemModelGenerator.register(ModItems.GOLDEN_HEART, Models.GENERATED);

        itemModelGenerator.register(ModItems.BACKPACK, Models.GENERATED);

        itemModelGenerator.register(ModItems.FOOD_CONDENSER, Models.GENERATED);

        //items coin
        itemModelGenerator.register(ModItems.BLUECOIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.REDCOIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.YELLOWCOIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLACKCOIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.GREENCOIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVERCOIN, Models.GENERATED);

        itemModelGenerator.register(ModItems.SUN_INGOT, Models.GENERATED);

        itemModelGenerator.register(ModItems.PLUG, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPLINTER, Models.GENERATED);
    }
}
