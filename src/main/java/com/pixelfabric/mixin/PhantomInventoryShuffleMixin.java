package com.pixelfabric.mixin;

import com.pixelfabric.commands.PhantomInventoryShuffleMechanic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(PhantomEntity.class)
public abstract class PhantomInventoryShuffleMixin {
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onPhantomTickMovement(CallbackInfo ci) {
        PhantomEntity phantom = (PhantomEntity) (Object) this;
        Entity target = phantom.getTarget();

        if (target instanceof PlayerEntity player && PhantomInventoryShuffleMechanic.isInventoryShuffleActive()) {
            shufflePlayerInventory(player);
        }
    }

    private void shufflePlayerInventory(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        Random random = new Random();

        // Crear una lista de todos los slots con items
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                items.add(stack.copy());
                inventory.setStack(i, ItemStack.EMPTY);
            }
        }

        // Revolver la lista
        Collections.shuffle(items, random);

        // Volver a colocar los items en slots aleatorios
        int currentSlot = 0;
        for (ItemStack item : items) {
            while (currentSlot < inventory.size() && !inventory.getStack(currentSlot).isEmpty()) {
                currentSlot++;
            }
            if (currentSlot < inventory.size()) {
                inventory.setStack(currentSlot, item);
                currentSlot++;
            }
        }

        // Reproducir un sonido para indicar que el inventario fue revuelto
        player.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 1.0F, 1.0F);
    }
}
