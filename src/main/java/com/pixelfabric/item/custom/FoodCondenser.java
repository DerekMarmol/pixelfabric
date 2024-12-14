package com.pixelfabric.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;


public class FoodCondenser extends Item {
    private static final int INVENTORY_SIZE = 9;
    private static final String FOOD_ITEMS_KEY = "FoodItems";
    private static final String LAST_USE_TIME_KEY = "LastUseTime";
    private static final int CONDENSATION_COOLDOWN = 5 * 60 * 20; // 5 minutos en ticks
    private static final int CONDENSATION_THRESHOLD = 5;

    public FoodCondenser(Settings settings) {
        super(settings.maxCount(1)); // Solo un condensador por inventario
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            NbtCompound nbt = stack.getOrCreateNbt();
            long currentTime = world.getTime();
            long lastUseTime = nbt.getLong(LAST_USE_TIME_KEY);

            // Verificar el tiempo de enfriamiento
            if (currentTime - lastUseTime < CONDENSATION_COOLDOWN) {
                player.sendMessage(Text.translatable("§cEl condensador aún no está listo. Espera.").formatted(Formatting.RED), true);
                return TypedActionResult.fail(stack);
            }

            SimpleInventory inventory = new SimpleInventory(INVENTORY_SIZE) {
                @Override
                public void onClose(PlayerEntity player) {
                    super.onClose(player);
                    if (condenseFoodItems(stack, this, player, world)) {
                        // Actualizar el tiempo de último uso solo si la condensación fue exitosa
                        stack.getOrCreateNbt().putLong(LAST_USE_TIME_KEY, currentTime);
                    }
                }
            };

            loadInventory(stack, inventory);

            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, playerInventory, playerEntity) ->
                            new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, // Cambiar a 9X1
                                    syncId,
                                    playerInventory,
                                    inventory,
                                    1), // 1 row
                    Text.translatable("Condensador de alimentos")
            ));
        }

        return TypedActionResult.success(stack);
    }

    // Método para cargar el inventario desde NBT
    private void loadInventory(ItemStack stack, SimpleInventory inventory) {
        if (stack.hasNbt() && stack.getNbt().contains(FOOD_ITEMS_KEY)) {
            NbtList items = stack.getNbt().getList(FOOD_ITEMS_KEY, 10);

            for (int i = 0; i < items.size(); i++) {
                NbtCompound itemTag = items.getCompound(i);
                int slot = itemTag.getInt("Slot");

                if (slot >= 0 && slot < inventory.size()) {
                    inventory.setStack(slot, ItemStack.fromNbt(itemTag));
                }
            }
        }
    }

    // Método para guardar el inventario en NBT
    private void saveInventory(ItemStack stack, SimpleInventory inventory) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList items = new NbtList();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slotStack = inventory.getStack(i);
            if (!slotStack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                slotStack.writeNbt(itemTag);
                items.add(itemTag);
            }
        }

        nbt.put(FOOD_ITEMS_KEY, items);
    }

    private boolean condenseFoodItems(ItemStack condenserStack, SimpleInventory inventory, PlayerEntity player, World world) {
        Map<String, Integer> foodCounts = new HashMap<>();
        List<ItemStack> nonFoodItems = new ArrayList<>();
        List<ItemStack> sacrificedItems = new ArrayList<>();
        int totalFoodItems = 0;

        // Separar items de comida y otros items
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.isFood()) {
                    String foodType = itemStack.getItem().getTranslationKey();
                    int count = itemStack.getCount();
                    foodCounts.put(foodType, foodCounts.getOrDefault(foodType, 0) + count);
                    totalFoodItems += count;
                } else {
                    nonFoodItems.add(itemStack.copy());
                    sacrificedItems.add(itemStack.copy());
                }
            }
        }

        // Verificar si hay suficientes items para condensar
        boolean condensationPerformed = false;
        if (totalFoodItems >= CONDENSATION_THRESHOLD || !sacrificedItems.isEmpty()) {
            ItemStack condensedFood = createCondensedFood(world, sacrificedItems);

            // Intentar dar la comida condensada al jugador
            if (!player.getInventory().insertStack(condensedFood)) {
                player.dropItem(condensedFood, false);
            }

            condensationPerformed = true;
        }

        // Limpiar completamente el inventario
        inventory.clear();

        // CAMBIO CLAVE: NO reinsertamos los items no alimenticios
        // Esto asegura que todos los items sean consumidos

        if (condensationPerformed) {
            saveInventory(condenserStack, inventory);
            player.sendMessage(Text.translatable("§aCondensación exitosa! Items consumidos.").formatted(Formatting.GREEN), true);
        } else {
            player.sendMessage(Text.translatable("§cCondensación fallida. Necesitas al menos 5 items o items valiosos para sacrificar.").formatted(Formatting.RED), true);
        }

        return condensationPerformed;
    }

    private double calculateDiversityBonus(List<ItemStack> sacrificedItems) {
        // Conjuntos para rastrear tipos únicos
        Set<Item> uniqueItemTypes = new HashSet<>();
        Set<String> itemCategories = new HashSet<>();

        // Mapas para categorías más detalladas
        Map<String, Integer> categoryWeights = new HashMap<>();
        categoryWeights.put("ore", 3);
        categoryWeights.put("ingot", 2);
        categoryWeights.put("tool", 2);
        categoryWeights.put("armor", 2);
        categoryWeights.put("block", 1);
        categoryWeights.put("misc", 1);

        double diversityMultiplier = 1.0;

        for (ItemStack item : sacrificedItems) {
            if (item.isEmpty()) continue;

            // Rastrear tipos únicos
            uniqueItemTypes.add(item.getItem());

            // Categorización avanzada
            String category = "misc";
            if (item.getItem() instanceof BlockItem) category = "block";
            if (item.getItem() instanceof ToolItem) category = "tool";
            if (item.getItem() instanceof ArmorItem) category = "armor";

            // Identificación de ores e ingots
            String itemName = item.getItem().toString().toLowerCase();
            if (itemName.contains("ore")) category = "ore";
            if (itemName.contains("ingot")) category = "ingot";

            itemCategories.add(category);
        }

        // Bonus por variedad de tipos de items
        if (uniqueItemTypes.size() > 5) {
            diversityMultiplier += 0.5; // Bonus por muchos tipos únicos
        }

        // Bonus por categorías diversas
        double categoryBonus = itemCategories.stream()
                .mapToDouble(category -> categoryWeights.getOrDefault(category, 1))
                .sum();

        diversityMultiplier += categoryBonus * 0.1;

        // Limitar el multiplicador
        return Math.min(diversityMultiplier, 3.0);
    }

    private ItemStack createCondensedFood(World world, List<ItemStack> sacrificedItems) {
        double totalSacrificeValue = calculateSacrificeValue(sacrificedItems);
        int totalItemCount = sacrificedItems.stream().mapToInt(ItemStack::getCount).sum();
        Random random = world.getRandom();

        // Aplicar el bonus de diversidad
        double diversityMultiplier = calculateDiversityBonus(sacrificedItems);

        // Sistema de generación de comida más equilibrado
        // Calcular múltiples factores
        double valueMultiplier = Math.log1p(totalSacrificeValue) / 5; // Escala logarítmica para suavizar
        int baseItemCount = Math.max(1, totalItemCount / 4); // Asegura una cantidad base de comida

        // Categorías de comida más dinámicas
        ItemStack condensedFood;
        if (totalSacrificeValue > 1000) {
            // Recompensas épicas para valores muy altos
            condensedFood = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE,
                    Math.min((int)(valueMultiplier * 2), 5));
        } else if (totalSacrificeValue > 500) {
            // Manzanas doradas en mayor cantidad
            condensedFood = new ItemStack(Items.GOLDEN_APPLE,
                    Math.min((int)(baseItemCount * valueMultiplier), 16));
        } else if (totalSacrificeValue > 250) {
            // Variedad de carnes cocidas
            ItemStack[] meatOptions = {
                    new ItemStack(Items.COOKED_BEEF),
                    new ItemStack(Items.COOKED_PORKCHOP),
                    new ItemStack(Items.COOKED_CHICKEN)
            };
            condensedFood = meatOptions[random.nextInt(meatOptions.length)];
            condensedFood.setCount(Math.min((int)(baseItemCount * valueMultiplier), 32));
        } else if (totalSacrificeValue > 100) {
            // Comidas más básicas
            ItemStack[] basicFoods = {
                    new ItemStack(Items.BREAD),
                    new ItemStack(Items.COOKED_MUTTON),
                    new ItemStack(Items.BAKED_POTATO)
            };
            condensedFood = basicFoods[random.nextInt(basicFoods.length)];
            condensedFood.setCount(Math.min((int)(baseItemCount * valueMultiplier * 1.5), 64));
        } else {
            // Fallback para valores bajos
            condensedFood = new ItemStack(Items.BREAD,
                    Math.max(1, (int)(baseItemCount * Math.sqrt(valueMultiplier))));
        }

        // Aplicar el multiplicador de diversidad al número de items
        condensedFood.setCount((int)(condensedFood.getCount() * diversityMultiplier));

        return condensedFood;
    }

    private double calculateSacrificeValue(List<ItemStack> items) {
        double totalValue = 0;
        for (ItemStack item : items) {
            double itemValue = getSacrificeValue(item);
            // Ajuste exponencial para dar más peso a items valiosos
            totalValue += Math.pow(itemValue * item.getCount(), 1.2);
        }
        return totalValue;
    }

    private double getSacrificeValue(ItemStack item) {
        // Sistema de valores más refinado
        if (item.isOf(Items.DIAMOND)) return 10;
        if (item.isOf(Items.NETHERITE_INGOT)) return 50;
        if (item.isOf(Items.GOLD_INGOT)) return 5;
        if (item.isOf(Items.IRON_INGOT)) return 2;

        // Categorías generales
        if (item.getItem() instanceof ToolItem) return 3;
        if (item.getItem() instanceof ArmorItem) return 4;

        // Bloques y recursos comunes con valores muy bajos
        if (item.isOf(Items.DIRT) || item.isOf(Items.STONE)) return 0.1;

        // Otros items con valor base
        return 1;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("§6Condensador de Supervivencia").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("§7Convierte items en comida según su valor").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("§e- Sacrifica items estratégicamente").formatted(Formatting.YELLOW));
        tooltip.add(Text.translatable("§e- Mayor valor = Mejor comida").formatted(Formatting.YELLOW));
    }
}
