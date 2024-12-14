package com.pixelfabric.item;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.ModEntities;
import com.pixelfabric.item.custom.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static com.pixelfabric.PixelFabric.MOD_ID;
import static com.pixelfabric.entity.ModEntities.*;


public class ModItems {

    public static final Item RUBY = registerItem("ruby", new Item(new FabricItemSettings()));
    public static final Item RAW_RUBY = registerItem("raw_ruby", new Item(new FabricItemSettings()));

    public static final Item PINATA_BURRITO_SPAWN_EGG = registerItem("pinata_burrito_spawn_egg",
            new SpawnEggItem(ModEntities.PINATA_BURRITO, 0xFFA500, 0xFFFFFF,
                    new FabricItemSettings()));

    public static final Item GOLDEN_MILK = registerItem("golden_milk",
            new ModFoodComponents.GoldenMilkItem(new FabricItemSettings().maxCount(1))
    );

    public static final Item SOLDIER_BEE_SPAWN_EGG = registerItem("soldier_bee_spawn_egg",
            new SpawnEggItem(ModEntities.Soldier_Bee,0xD57E36, 0x1D0D00,
            new FabricItemSettings()));

    public static final Item GOLEM_SPAWN_EGG= registerItem("golem_spawn_egg",
            new SpawnEggItem(ModEntities.Golem, 0xD57E36, 0x1D0D00,
                    new FabricItemSettings()));

    public static final Item WRAITH_SPAWN_EGG = registerItem("wraith_spawn_egg",
            new SpawnEggItem(ModEntities.Wraith,0xA3C1DA, 0x00FF00,
                    new FabricItemSettings()));

    public static final Item INFERNAL_BULL_EGG = registerItem("infernal_bull_egg",
            new SpawnEggItem(INFERNAL_BULL, -26164, -10079488,
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
            Registries.ITEM, new Identifier(MOD_ID, "skeleton_flyer_spawn_egg"),
            new SpawnEggItem(Skull, 0x808080, 0x000000, new Item.Settings())
    );

    public static final Item BARNACLE_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "barnacle_spawn_egg"),
            new SpawnEggItem(ModEntities.BARNACLE, 0x0000FF, 0xFF0000, new Item.Settings())
    );

    public static final Item MOOBLOOM_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "moobloom_spawn_egg"),
            new SpawnEggItem(ModEntities.MOOBLOOM, 0xFFFF00, 0xFFFFFF, new Item.Settings())
    );

    public static final Item CHAOS_PHANTOM_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "chaos_phantom_spawn_egg"),
            new SpawnEggItem(ModEntities.CHAOS_PHANTOM, 0x0000FF, 0xFFFFFF, new Item.Settings())
    );

    public static final Item BONE_SPIDER_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "bone_spider_spawn_egg"),
            new SpawnEggItem(ModEntities.BONE_SPIDER, 0x800080, 0xFFFFFF, new Item.Settings())
    );

    public static final Item MINER_ZOMBIE_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "miner_zombie_spawn_egg"),
            new SpawnEggItem(ModEntities.MINER_ZOMBIE, 0x00FF00, 0x808080, new Item.Settings())
    );

    public static final Item EXPLODING_SKELETON_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "exploding_skeleton_spawn_egg"),
            new SpawnEggItem(ModEntities.EXPLODING_SKELETON, 0x4F4F4F, 0xFF0000, new Item.Settings())
    );

    public static final Item CANDIK_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "candik_spawn_egg"),
            new SpawnEggItem(CANDIK, 0xD4AF37,0xFFA500, new Item.Settings())
    );

    public static final Item LAVAQUID_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "lavaquid_spawn_egg"),
            new SpawnEggItem(LAVASQUID, 0xFFD700, 0xFF4500, new Item.Settings())
    );

    //totems
    public static final Item HEART_TOTEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "heart_totem"),
            new HeartTotemItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE))
    );
    public static final Item REGENERATION_TOTEM = Registry.register(
            Registries.ITEM,
            new Identifier("pixelfabric", "regeneration_totem"),
            new RegenerationTotemItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE))
    );
    public static final Item INVENTORY_TOTEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "inventory_totem"),
            new InventoryTotemItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE))
    );

    //wither armor
    public static final Item WITHER_HELMET = registerItem("wither_helmet",
            new ModArmorItem(ModArmorMaterials.WITHER, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item WITHER_CHESTPLATE = registerItem("wither_chestplate",
            new ModArmorItem(ModArmorMaterials.WITHER, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item WITHER_LEGGINS = registerItem("wither_leggins",
            new ModArmorItem(ModArmorMaterials.WITHER, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item WITHER_BOOTS = registerItem("wither_boots",
            new ModArmorItem(ModArmorMaterials.WITHER, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    //wither tools
    public static final Item WITHER_SWORD = registerItem("wither_sword",
            new SwordItem(ModToolMaterial.WITHER, 6, 2.0f, new FabricItemSettings()));

    //wither tools part
    public static final Item WITHER_SWORD_PART1 = registerItem("wither_sword_part1", new Item(new FabricItemSettings()));
    public static final Item WITHER_SWORD_PART2 = registerItem("wither_sword_part2", new Item(new FabricItemSettings()));
    public static final Item IRON_REFORGED = registerItem("iron_reforged", new Item(new FabricItemSettings()));
    public static final Item SOUL_STAFF = registerItem("soul_staff", new Item(new FabricItemSettings()));
    public static final Item WITHER_DUST = registerItem("wither_dust", new Item(new FabricItemSettings()));
    public static final Item WITHE_NETHERITE_SCRAP = registerItem("withe_netherite_scrap", new Item(new FabricItemSettings()));

    //summertime armor
    public static final Item SUMMERTIME_HELMET = registerItem("summertime_helmet",
            new ModArmorItem(ModArmorMaterials.SUMMERTIME, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item SUMMERTIME_CHESTPLATE = registerItem("summertime_chestplate",
            new ModArmorItem(ModArmorMaterials.SUMMERTIME, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item SUMMERTIME_LEGGINS = registerItem("summertime_leggins",
            new ModArmorItem(ModArmorMaterials.SUMMERTIME, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item SUMMERTIME_BOOTS = registerItem("summertime_boots",
            new ModArmorItem(ModArmorMaterials.SUMMERTIME, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    //items
    public static final Item HEART_CONTAINER = registerItem("heart_container",
            new HeartContainer(new FabricItemSettings()
                    .maxCount(16)
                    .rarity(Rarity.RARE))
    );

    public static final Item GOLDEN_HEART = registerItem("golden_heart",
            new GoldenHeart(new FabricItemSettings().
                    maxCount(16)
                    .rarity(Rarity.EPIC))
    );

    //item bone spider
    public static final Item BONE_SPIDER_JAWBONE = registerItem("bone_spider_jawbone",
            new Item(new FabricItemSettings()));

    //items coins
    public static final Item BLUECOIN = registerItem("bluecoin",
            new Item(new FabricItemSettings()));
    public static final Item REDCOIN= registerItem("redcoin",
            new Item(new FabricItemSettings()));
    public static final Item YELLOWCOIN = registerItem("yellowcoin",
            new Item(new FabricItemSettings()));
    public static final Item GREENCOIN = registerItem("greencoin",
            new Item(new FabricItemSettings()));
    public static final Item SILVERCOIN = registerItem("silvercoin",
            new Item(new FabricItemSettings()));
    public static final Item BLACKCOIN = registerItem("blackcoin",
            new Item(new FabricItemSettings()));

    public static final Item SUN_INGOT = registerItem("sun_ingot",
            new SunIngot(new FabricItemSettings()));

    public static final Item PLUG = registerItem("plug",
            new Item(new FabricItemSettings()));

    public static final Item SPLINTER = registerItem("splinter",
            new Item(new FabricItemSettings()));

    public static final Backpack BACKPACK = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "backpack"),
            new Backpack(new FabricItemSettings().maxCount(1))
    );

    public static final FoodCondenser FOOD_CONDENSER = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "food_condenser"),
            new FoodCondenser(new FabricItemSettings().maxCount(1))
    );

    public static final Item PINATABAT_EM = registerItem("pinatabat_em",
            new Item(new FabricItemSettings().maxCount(1)));

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries){
        entries.add(RUBY);
        entries.add(RAW_RUBY);

        entries.add(BARNACLE_SPAWN_EGG);
        entries.add(LAVAQUID_SPAWN_EGG);
        entries.add(CANDIK_SPAWN_EGG);
        entries.add(HELLHOUND_SPAWN_EGG);
        entries.add(BONE_SPIDER_JAWBONE);
        entries.add(SOLDIER_BEE_SPAWN_EGG);
        entries.add(GOLEM_SPAWN_EGG);
        entries.add(LAVA_SPIDER_SPAWN_EGG);
        entries.add(WRAITH_SPAWN_EGG);
        entries.add(CANDLE_SWORD_SPAWN_EGG);
        entries.add(WILDFIRE_SPAWN_EGG);
        entries.add(SKELETON_FLYER_SPAWN_EGG);
        entries.add(BONE_SPIDER_SPAWN_EGG);
        entries.add(EXPLODING_SKELETON_SPAWN_EGG);
        entries.add(MINER_ZOMBIE_SPAWN_EGG);
        entries.add(MOOBLOOM_SPAWN_EGG);
        entries.add(CHAOS_PHANTOM_SPAWN_EGG);

        entries.add(GOLDEN_MILK);

        entries.add(PINATABAT_EM);

        entries.add(INVENTORY_TOTEM);
        entries.add(HEART_TOTEM);
        entries.add(REGENERATION_TOTEM);

        entries.add(ModItems.WITHER_SWORD);
        entries.add(ModItems.WITHER_SWORD_PART1);
        entries.add(ModItems.WITHER_SWORD_PART2);

        entries.add(ModItems.WITHE_NETHERITE_SCRAP);

        entries.add(HEART_CONTAINER);
        entries.add(GOLDEN_HEART);

        entries.add(BLUECOIN);
        entries.add(BLACKCOIN);
        entries.add(SILVERCOIN);
        entries.add(GREENCOIN);
        entries.add(REDCOIN);
        entries.add(YELLOWCOIN);

        entries.add(FOOD_CONDENSER);
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
