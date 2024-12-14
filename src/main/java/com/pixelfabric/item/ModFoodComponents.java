package com.pixelfabric.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ModFoodComponents {
    public static class GoldenMilkItem extends MilkBucketItem {
        public GoldenMilkItem(Settings settings) {
            super(settings);
        }

        @Override
        public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
            if (user instanceof PlayerEntity player) {
                // Primero aplicamos los efectos
                if (!world.isClient) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1700, 2));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1700, 2));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1500, 2));
                }

                // Después manejamos el inventario si no está en creativo
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                    if (stack.isEmpty()) {
                        return new ItemStack(Items.BUCKET);
                    }
                    player.getInventory().insertStack(new ItemStack(Items.BUCKET));
                }
            }

            return stack;
        }
    }
}
