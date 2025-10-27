package com.pixelfabric.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class ShieldRepairMixin {

    @Inject(method = "updateResult", at = @At("TAIL"))
    private void onAnvilRepair(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
        ItemStack result = handler.getSlot(2).getStack();

        // Si el resultado es un escudo, limpiar el contador
        if (result.getItem() == Items.SHIELD && result.hasNbt()) {
            result.getNbt().remove("fragile_shield_hits");
        }
    }
}
