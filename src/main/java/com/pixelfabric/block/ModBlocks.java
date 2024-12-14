package com.pixelfabric.block;

import com.pixelfabric.PixelFabric;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    // Primero declaramos el bloque
    public static final Block WHITE_NETHERITE_BLOCK = registerBlock("withe_netherite_block",
            new Block(AbstractBlock.Settings.create().strength(3.5f)
                    .requiresTool().sounds(BlockSoundGroup.ANCIENT_DEBRIS)));

    public static final Block RUNA_CELEST = registerBlock("runa_celest",
            new RunaCelestBlock());

    public static final Block BARBED_WIRE_FENCE = registerBlock("barbed_wire_fence",
            new BlockBarbedWireFence(FabricBlockSettings.create()
                    .strength(2.0f)
                    .requiresTool()
                    .nonOpaque()
                    .sounds(BlockSoundGroup.METAL)
                    .noCollision()));

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(PixelFabric.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block){
        Registry.register(Registries.ITEM, new Identifier(PixelFabric.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks(){
        PixelFabric.LOGGER.info("Registrado bloques para pixelfabric " + PixelFabric.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.WHITE_NETHERITE_BLOCK);
            entries.add(ModBlocks.RUNA_CELEST);
            entries.add(ModBlocks.BARBED_WIRE_FENCE);
        });
    }
}