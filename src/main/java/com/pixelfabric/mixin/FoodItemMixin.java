package com.pixelfabric.mixin;

import com.pixelfabric.commands.BadFoodMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class FoodItemMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void onFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack) (Object) this; // Cast this to ItemStack
        if (stack.getItem().isFood() && BadFoodMode.shouldFoodGiveNegativeEffect()) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 200, 1));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0));
            if (user instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) user).sendMessage(Text.literal("¡La comida te ha enfermado!"), true);
            }
        }
    }
}
