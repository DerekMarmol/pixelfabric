package com.pixelfabric.item;

import com.pixelfabric.Enchantments.ModEnchantments;
import com.pixelfabric.PixelFabric;
import com.pixelfabric.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.pixelfabric.item.ModItems.*;

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
                        entries.add(ModItems.LAVAQUID_SPAWN_EGG);
                        entries.add(ModItems.PUMPKIN_FIEND_SPAWN_EGG);
                        entries.add(ModItems.BARNACLE_SPAWN_EGG);
                        entries.add(ModItems.CANDIK_SPAWN_EGG);
                        entries.add(ModItems.LAVA_SPIDER_SPAWN_EGG);
                        entries.add(ModItems.SKELETON_FLYER_SPAWN_EGG);
                        entries.add(BONE_SPIDER_SPAWN_EGG);
                        entries.add(EXPLODING_SKELETON_SPAWN_EGG);
                        entries.add(MINER_ZOMBIE_SPAWN_EGG);
                        entries.add(MOOBLOOM_SPAWN_EGG);
                        entries.add(CHAOS_PHANTOM_SPAWN_EGG);

                        entries.add(ModItems.GOLDEN_MILK);
                        entries.add(HEART_CONTAINER);

                        entries.add(ModItems.WITHER_BOOTS);
                        entries.add(ModItems.WITHER_CHESTPLATE);
                        entries.add(ModItems.WITHER_HELMET);
                        entries.add(ModItems.WITHER_LEGGINS);

                        entries.add(ModBlocks.BARBED_WIRE_FENCE);

                        entries.add(ModItems.WITHER_SWORD);
                        entries.add(ModItems.WITHER_SWORD_PART1);
                        entries.add(ModItems.WITHER_SWORD_PART2);
                        entries.add(WITHER_DUST);
                        entries.add(SOUL_STAFF);
                        entries.add(IRON_REFORGED);

                        entries.add(ModBlocks.WHITE_NETHERITE_BLOCK);
                        entries.add(ModItems.WITHE_NETHERITE_SCRAP);

                        entries.add(ModItems.SUMMERTIME_HELMET);
                        entries.add(ModItems.SUMMERTIME_CHESTPLATE);
                        entries.add(ModItems.SUMMERTIME_LEGGINS);
                        entries.add(ModItems.SUMMERTIME_BOOTS);

                        entries.add(ModItems.REGENERATION_TOTEM);
                        entries.add(ModItems.INVENTORY_TOTEM);
                        entries.add(ModItems.HEART_TOTEM);
                        entries.add(GOLDEN_HEART);

                        entries.add(BONE_SPIDER_JAWBONE);

                        entries.add(BLUECOIN);
                        entries.add(BLACKCOIN);
                        entries.add(SILVERCOIN);
                        entries.add(GREENCOIN);
                        entries.add(REDCOIN);
                        entries.add(YELLOWCOIN);

                        entries.add(BACKPACK);
                        entries.add(SUN_INGOT);

                        entries.add(ModBlocks.RUNA_CELEST);

                        entries.add(INFERNAL_BULL_EGG);

                        entries.add(PINATABAT_EM);
                        entries.add(PINATA_BURRITO_SPAWN_EGG);

                        entries.add(FOOD_CONDENSER);
                    }).build());

    public static void registerItemGroups(){
        PixelFabric.LOGGER.info("Registrando ItemGroup para " + PixelFabric.MOD_ID);
    }
}
