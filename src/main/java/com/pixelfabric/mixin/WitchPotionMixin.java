// Archivo: mixin/WitchPotionInterceptMixin.java
package com.pixelfabric.mixin;

import com.pixelfabric.commands.CorruptWitchMechanic;
import com.pixelfabric.effects.ModEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WitchEntity.class)
public class WitchPotionMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void interceptPotionThrow(CallbackInfo ci) {
        // Solo si la mecánica está activa
        if (!CorruptWitchMechanic.isCorruptWitchActive()) {
            return;
        }

        WitchEntity witch = (WitchEntity) (Object) this;

        // Solo en el servidor
        if (witch.getWorld().isClient) {
            return;
        }

        // Verificar si hay un jugador objetivo cerca
        PlayerEntity targetPlayer = witch.getWorld().getClosestPlayer(witch, 16.0);
        if (targetPlayer == null || targetPlayer.isCreative() || targetPlayer.isSpectator()) {
            return;
        }

        // Solo si la bruja puede atacar (no está en cooldown y tiene línea de vista)
        if (witch.getTarget() == targetPlayer && witch.age % 40 == 0) { // Cada 2 segundos (era 60 = 3 segundos)

            // 50% de probabilidad de lanzar poción de controles invertidos (era 25%)
            if (witch.getRandom().nextFloat() < 0.5f) {

                // Crear la poción
                ItemStack invertedPotion = new ItemStack(Items.SPLASH_POTION);
                StatusEffectInstance invertedEffect = new StatusEffectInstance(
                        ModEffects.INVERTED_CONTROLS_EFFECT,
                        200, // 10 segundos
                        0
                );
                PotionUtil.setCustomPotionEffects(invertedPotion, List.of(invertedEffect));

                // Lanzar la poción
                PotionEntity potionEntity = new PotionEntity(witch.getWorld(), witch);
                potionEntity.setItem(invertedPotion);

                // Calcular dirección hacia el jugador
                double deltaX = targetPlayer.getX() - witch.getX();
                double deltaY = targetPlayer.getEyeY() - 1.1 - witch.getY();
                double deltaZ = targetPlayer.getZ() - witch.getZ();
                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                potionEntity.setVelocity(
                        deltaX,
                        deltaY + distance * 0.2,
                        deltaZ,
                        1.6f,
                        (float) (14 - witch.getWorld().getDifficulty().getId() * 4)
                );

                witch.getWorld().spawnEntity(potionEntity);
            }
        }
    }
}