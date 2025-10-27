// Actualiza tu ModEffects.java
package com.pixelfabric.effects;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.effects.custom.Inmunity;
import com.pixelfabric.effects.custom.InvertedControlsEffect;
import com.pixelfabric.effects.custom.SoulProtectionEffect;
import com.pixelfabric.effects.custom.ZombificationEffect;
import com.pixelfabric.effects.custom.FeatherweightEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.pixelfabric.PixelFabric.MOD_ID;

public class ModEffects {
    public static StatusEffect INMUNITY_EFFECT;
    public static StatusEffect ZOMBIFICATION_EFFECT;
    public static StatusEffect SOUL_PROTECTION_EFFECT;
    public static StatusEffect INVERTED_CONTROLS_EFFECT;
    public static StatusEffect FEATHERWEIGHT_EFFECT; // Nuevo efecto

    public static StatusEffect registerStatusEffect(String name, StatusEffect effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, name), effect);
    }

    public static void registerEffects() {
        PixelFabric.LOGGER.info("Registrando efectos por " + MOD_ID);
        INMUNITY_EFFECT = registerStatusEffect("inmunity", new Inmunity(StatusEffectCategory.HARMFUL, 0xFFFFFF));
        ZOMBIFICATION_EFFECT = registerStatusEffect("zombification", new ZombificationEffect(StatusEffectCategory.HARMFUL, 0x00FF00));
        SOUL_PROTECTION_EFFECT = registerStatusEffect("soul_protection", new SoulProtectionEffect(StatusEffectCategory.BENEFICIAL, 0x9900FF));
        INVERTED_CONTROLS_EFFECT = registerStatusEffect("inverted_controls", new InvertedControlsEffect(StatusEffectCategory.HARMFUL, 0xFF00FF));
        FEATHERWEIGHT_EFFECT = registerStatusEffect("featherweight", new FeatherweightEffect(StatusEffectCategory.BENEFICIAL, 0x87CEEB)); // Color azul cielo
    }
}