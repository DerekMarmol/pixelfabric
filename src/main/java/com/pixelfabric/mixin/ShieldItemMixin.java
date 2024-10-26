package com.pixelfabric.mixin;

import com.pixelfabric.commands.UnstableShields;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShieldItem.class)
public class ShieldItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUseShield(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        if (UnstableShields.shouldShieldBreak()) {
            stack.decrement(1); // Rompe el escudo
            cir.cancel(); // Evita el uso normal del escudo
        }
    }
}
