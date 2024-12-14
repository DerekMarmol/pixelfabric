package com.pixelfabric.mixin;

import com.pixelfabric.mission.CraftingMissionListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin {
    @Shadow
    @Final
    private PlayerEntity player;

    @Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void onItemCrafted(ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity) {
            CraftingMissionListener.checkInventory((ServerPlayerEntity) player);
        }
    }
}