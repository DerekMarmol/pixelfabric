package com.pixelfabric.Enchantments;

import com.pixelfabric.Enchantments.custom.BleedEnchantment;
import com.pixelfabric.Enchantments.custom.ResonanceEnchantment;
import com.pixelfabric.Enchantments.custom.VampireEnchantment;
import com.pixelfabric.PixelFabric;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final Enchantment VAMPIRE = registerEnchantment("vampire",
            new VampireEnchantment());
    public static final Enchantment BLEED = registerEnchantment("bleed",
            new BleedEnchantment());
    public static final Enchantment RESONANCE = registerEnchantment("resonance",
            new ResonanceEnchantment());
    private static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT,
                new Identifier(PixelFabric.MOD_ID, name),
                enchantment);
    }

    public static void registerModEnchantments() {
        PixelFabric.LOGGER.info("Registering Enchantments for " + PixelFabric.MOD_ID);
    }
}