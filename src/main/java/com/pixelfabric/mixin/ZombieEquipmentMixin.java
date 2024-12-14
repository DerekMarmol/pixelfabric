// ZombieEquipmentMixin.java
package com.pixelfabric.mixin;

import com.pixelfabric.commands.ZombieEnhancementMechanic;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public class ZombieEquipmentMixin {

    @Inject(method = "initEquipment", at = @At("TAIL"))
    private void addZombieEquipment(CallbackInfo ci) {
        if (ZombieEnhancementMechanic.isZombieEnhancementActive()) {
            ZombieEntity zombie = (ZombieEntity) (Object) this;

            // Verificar probabilidad del 40%
            if (Math.random() < 0.4) {
                // Si la mano principal está vacía, añadir el tótem
                if (zombie.getMainHandStack().isEmpty()) {
                    ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
                    zombie.equipStack(EquipmentSlot.MAINHAND, totem);
                    zombie.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
                }
            }
        }
    }
}
