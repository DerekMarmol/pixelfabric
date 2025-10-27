package com.pixelfabric.mixin;

import com.pixelfabric.commands.GluttonousZombiesMechanic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class GluttonousZombiesMixin {

    @Inject(method = "tryAttack", at = @At("RETURN"))
    private void onZombieAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        // Solo si la mecánica está activa
        if (!GluttonousZombiesMechanic.isGluttonousZombiesActive()) {
            return;
        }

        // Solo si el ataque fue exitoso
        if (!cir.getReturnValue()) {
            return;
        }

        // Solo si el objetivo es un jugador
        if (!(target instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) target;
        ZombieEntity zombie = (ZombieEntity) (Object) this;

        // Solo en el servidor
        if (player.getWorld().isClient) {
            return;
        }

        // No afectar jugadores en creativo o espectador
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        // Reducir 1 punto de comida (medio muslito)
        // getFoodLevel() devuelve el nivel actual de comida (0-20)
        int currentFood = serverPlayer.getHungerManager().getFoodLevel();

        if (currentFood > 0) {
            // Reducir 1 punto de comida
            serverPlayer.getHungerManager().setFoodLevel(Math.max(0, currentFood - 1));

            // También reducir un poco la saturación para que el hambre baje más rápido
            float currentSaturation = serverPlayer.getHungerManager().getSaturationLevel();
            serverPlayer.getHungerManager().setSaturationLevel(Math.max(0, currentSaturation - 1.0f));

            // Sonido de "comida siendo consumida" pero más siniestro
            serverPlayer.getWorld().playSound(
                    null,
                    serverPlayer.getX(),
                    serverPlayer.getY(),
                    serverPlayer.getZ(),
                    SoundEvents.ENTITY_GENERIC_EAT,
                    SoundCategory.HOSTILE,
                    1.0f,
                    0.5f  // Tono más bajo para hacerlo más siniestro
            );

            /* serverPlayer.sendMessage(
                    Text.literal("§4¡El zombie devoró parte de tu energía!"),
                    true // true = actionbar, false = chat
            );*/
        }
    }
}