package com.pixelfabric.utils;

import com.pixelfabric.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {
    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Verificar si es la tabla de botín de los esqueletos wither
            if (source.isBuiltin() && id.equals(EntityType.WITHER_SKELETON.getLootTableId())) {
                // Agregar el nuevo item a la tabla de botín
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.WITHER_DUST))
                        .conditionally(RandomChanceLootCondition.builder(0.20f)); // 0.1% de probabilidad

                tableBuilder.pool(poolBuilder.build());
            }
        });
    }

    public static void init() {
        modifyLootTables();
    }
}