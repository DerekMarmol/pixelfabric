package com.pixelfabric.item;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import static com.pixelfabric.PixelFabric.MOD_ID;
import static com.pixelfabric.entity.ModEntities.*;


public class ModItems {

    public static final Item RUBY = registerItem("ruby", new Item(new FabricItemSettings()));
    public static final Item RAW_RUBY = registerItem("raw_ruby", new Item(new FabricItemSettings()));

    public static final Item SOLDIER_BEE_SPAWN_EGG = registerItem("soldier_bee_spawn_egg",
            new SpawnEggItem(ModEntities.Soldier_Bee,0xD57E36, 0x1D0D00,
            new FabricItemSettings()));

    public static final Item GOLEM_SPAWN_EGG= registerItem("golem_spawn_egg",
            new SpawnEggItem(ModEntities.Golem, 0xD57E36, 0x1D0D00,
                    new FabricItemSettings()));

    public static final Item WRAITH_SPAWN_EGG = registerItem("wraith_spawn_egg",
            new SpawnEggItem(ModEntities.Wraith,0xA3C1DA, 0x00FF00,
                    new FabricItemSettings()));

    public static final Item WILDFIRE_SPAWN_EGG = registerItem("wildfire_spawn_egg",
            new SpawnEggItem(ModEntities.Wildfire, 0xFF6A00, 0x8B0000,
                    new FabricItemSettings()));

    public static final Item LAVA_SPIDER_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "lava_spider_spawn_egg"),
            new SpawnEggItem(LAVA_SPIDER, 0xFF6A00, 0xFFD700, new Item.Settings())  // Colores en hexadecimal
    );

    public static final Item CANDLE_SWORD_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "candle_sword_spawn_egg"),
            new SpawnEggItem(CANDLE_SWORD, 0xFFD700, 0xFF4500, new Item.Settings())  // Colores en hexadecimal
    );

    public static final Item HELLHOUND_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "hellhound_spawn_egg"),
            new SpawnEggItem(Hellhound, 0x000000, 0xFFFFFF, new Item.Settings())
    );

    public static final Item PUMPKIN_FIEND_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "pumpkin_fiend_spawn_egg"),
            new SpawnEggItem(Pumpkin, 0xD2691E, 0x006400, new Item.Settings())
    );

    public static final Item SKELETON_FLYER_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier("pixelfabric", "skeleton_flyer_spawn_egg"),
            new SpawnEggItem(Skull, 0x808080, 0x000000, new Item.Settings())
    );

    public static final Item BARNACLE_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier("pixelfabric", "barnacle_spawn_egg"),
            new SpawnEggItem(ModEntities.BARNACLE, 0x0000FF, 0xFF0000, new Item.Settings())
    );

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries){
        entries.add(RUBY);
        entries.add(BARNACLE_SPAWN_EGG);
        entries.add(RAW_RUBY);
        entries.add(HELLHOUND_SPAWN_EGG);
        entries.add(SOLDIER_BEE_SPAWN_EGG);
        entries.add(GOLEM_SPAWN_EGG);
        entries.add(LAVA_SPIDER_SPAWN_EGG);
        entries.add(WRAITH_SPAWN_EGG);
        entries.add(CANDLE_SWORD_SPAWN_EGG);
        entries.add(WILDFIRE_SPAWN_EGG);
        entries.add(SKELETON_FLYER_SPAWN_EGG);
    }

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
    }

    public static void registerModItems(){
        PixelFabric.LOGGER.info("Registrando Items Mod para " + MOD_ID);

        RegistryKey<ItemGroup> ingredientsGroupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("minecraft", "ingredients"));

        ItemGroupEvents.modifyEntriesEvent(ingredientsGroupKey).register(ModItems::addItemsToIngredientItemGroup);
    }
}
