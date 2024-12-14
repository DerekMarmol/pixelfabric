package com.pixelfabric.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class Backpack extends Item {
    private static final int INVENTORY_SIZE = 27;
    private static final String ITEMS_KEY = "Items";

    public Backpack(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            // Crear el inventario y cargarlo desde NBT
            SimpleInventory inventory = new SimpleInventory(INVENTORY_SIZE) {
                @Override
                public void onClose(PlayerEntity player) {
                    super.onClose(player);
                    // Guardar el inventario en NBT cuando se cierra
                    saveInventory(stack, this);
                }
            };

            // Cargar items guardados
            loadInventory(stack, inventory);

            // Abrir la GUI del inventario
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, playerInventory, playerEntity) ->
                            GenericContainerScreenHandler.createGeneric9x3(
                                    syncId,
                                    playerInventory,
                                    inventory
                            ),
                    Text.translatable("Mochila")
            ));
        }

        return TypedActionResult.success(stack);
    }

    // Método para guardar el inventario en NBT
    private void saveInventory(ItemStack stack, SimpleInventory inventory) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList items = new NbtList();

        // Guardar cada slot del inventario
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slotStack = inventory.getStack(i);
            if (!slotStack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                slotStack.writeNbt(itemTag);
                items.add(itemTag);
            }
        }

        nbt.put(ITEMS_KEY, items);
    }

    // Método para cargar el inventario desde NBT
    private void loadInventory(ItemStack stack, SimpleInventory inventory) {
        if (stack.hasNbt() && stack.getNbt().contains(ITEMS_KEY)) {
            NbtList items = stack.getNbt().getList(ITEMS_KEY, 10); // 10 es el tipo de NBT para compounds

            // Cargar cada item guardado en su slot correspondiente
            for (int i = 0; i < items.size(); i++) {
                NbtCompound itemTag = items.getCompound(i);
                int slot = itemTag.getInt("Slot");

                if (slot >= 0 && slot < inventory.size()) {
                    inventory.setStack(slot, ItemStack.fromNbt(itemTag));
                }
            }
        }
    }

    // Opcional: Mostrar si la mochila tiene items
    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt() && !stack.getNbt().getList(ITEMS_KEY, 10).isEmpty();
    }
}