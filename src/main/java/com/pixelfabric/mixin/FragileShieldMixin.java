package com.pixelfabric.mixin;

import com.pixelfabric.commands.FragileShieldsMechanic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class FragileShieldMixin {

    @Inject(method = "blockedByShield", at = @At("RETURN"))
    private void onShieldBlock(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        // Solo si la mecánica está activa
        if (!FragileShieldsMechanic.isFragileShieldsActive()) {
            return;
        }

        // Solo aplicar a jugadores
        if (!((Object) this instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;

        // Solo si efectivamente bloqueó con escudo
        if (!cir.getReturnValue()) {
            return;
        }

        // Obtener el escudo (puede estar en cualquier mano)
        ItemStack shield = null;
        if (player.getMainHandStack().getItem() == Items.SHIELD) {
            shield = player.getMainHandStack();
        } else if (player.getOffHandStack().getItem() == Items.SHIELD) {
            shield = player.getOffHandStack();
        }

        if (shield == null) {
            return;
        }

        // Obtener o inicializar el contador de golpes
        NbtCompound nbt = shield.getOrCreateNbt();
        int hitCount = nbt.getInt("fragile_shield_hits") + 1;
        nbt.putInt("fragile_shield_hits", hitCount);

        // Si llegó a 5 golpes, romper el escudo
        if (hitCount >= 5) {
            // Efecto de rotura
            player.getWorld().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.ITEM_SHIELD_BREAK,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f
            );

            // Romper el escudo (reducir stack a 0)
            shield.setCount(0);
        }
    }
}