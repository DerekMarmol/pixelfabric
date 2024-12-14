package com.pixelfabric.mixin;

import com.pixelfabric.mission.ArmorEquipListener;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "equipStack", at = @At("TAIL"))
    private void onEquipArmor(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        // Obtener la instancia actual como PlayerEntity
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;

        // Verificar si es un ServerPlayerEntity y si es una pieza de armadura
        if (slot.getType() == EquipmentSlot.Type.ARMOR && thisPlayer instanceof ServerPlayerEntity) {
            ArmorEquipListener.onArmorEquip((ServerPlayerEntity) thisPlayer);
        }
    }
}