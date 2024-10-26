package com.pixelfabric.item;

import com.pixelfabric.PixelFabric;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemsGroups {

    public static final ItemGroup RUBY_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(PixelFabric.MOD_ID, "ruby"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.ruby"))
                    .icon(() -> new ItemStack(ModItems.RUBY)).entries((displayContext, entries) -> {
                        entries.add(ModItems.RUBY);
                        entries.add(ModItems.RAW_RUBY);
                        entries.add(ModItems.SOLDIER_BEE_SPAWN_EGG);
                        entries.add(ModItems.GOLEM_SPAWN_EGG);
                        entries.add(ModItems.WRAITH_SPAWN_EGG);
                        entries.add(ModItems.HELLHOUND_SPAWN_EGG);
                        entries.add(ModItems.WILDFIRE_SPAWN_EGG);
                        entries.add(ModItems.CANDLE_SWORD_SPAWN_EGG);
                        entries.add(ModItems.PUMPKIN_FIEND_SPAWN_EGG);
                        entries.add(ModItems.BARNACLE_SPAWN_EGG);
                        entries.add(ModItems.LAVA_SPIDER_SPAWN_EGG);
                        entries.add(ModItems.SKELETON_FLYER_SPAWN_EGG);

                    }).build());

    public static void registerItemGroups(){
        PixelFabric.LOGGER.info("Registrando ItemGroup para " + PixelFabric.MOD_ID);
    }
}
