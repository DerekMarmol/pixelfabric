package com.pixelfabric.mixin;

import com.pixelfabric.commands.PiercingArrowsMechanic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PiercingArrowsMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onArrowDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Solo si la mecánica está activa
        if (!PiercingArrowsMechanic.isPiercingArrowsActive()) {
            return;
        }

        // Solo aplicar a jugadores
        if (!((Object) this instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;

        // Solo en el servidor
        if (player.getWorld().isClient) {
            return;
        }

        // No afectar jugadores en creativo o espectador
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        // Verificar que el daño sea de una flecha
        if (!source.isOf(DamageTypes.ARROW)) {
            return;
        }

        // Verificar que la flecha venga de un esqueleto
        if (source.getSource() instanceof ArrowEntity) {
            ArrowEntity arrow = (ArrowEntity) source.getSource();

            // Verificar que el dueño de la flecha sea un esqueleto
            if (arrow.getOwner() instanceof AbstractSkeletonEntity) {
                // Cancelar el daño original
                cir.setReturnValue(false);

                // Calcular daño perforante (ignora 75% de la armadura)
                float piercingDamage = calculatePiercingDamage(player, amount);

                // Crear un nuevo DamageSource personalizado para el daño perforante
                DamageSource piercingSource = player.getDamageSources().magic();

                // Aplicar el daño perforante directamente
                player.damage(piercingSource, piercingDamage);

                // Efecto visual y sonoro
                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ITEM_CROSSBOW_HIT,
                        SoundCategory.HOSTILE,
                        0.8f,
                        1.2f
                );

                // Mensaje opcional (comentado para no spamear)
                /*
                if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) player).sendMessage(
                            Text.literal("§c¡Flecha perforante!"),
                            true
                    );
                }
                */
            }
        }
    }

    private float calculatePiercingDamage(PlayerEntity player, float originalDamage) {
        // Obtener la protección total de armadura del jugador
        float armorValue = player.getArmor();
        float toughness = (float) player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        // Calcular la reducción de daño normal de la armadura
        float normalReduction = calculateArmorReduction(originalDamage, armorValue, toughness);
        float normalDamage = originalDamage - normalReduction;

        // Para flechas perforantes, solo aplicamos 25% de la reducción de armadura
        // (es decir, ignoramos 75% de la protección)
        float piercingReduction = normalReduction * 0.25f;
        float piercingDamage = originalDamage - piercingReduction;

        // Asegurar que el daño perforante sea mayor al normal pero no excesivo
        return Math.max(normalDamage, Math.min(piercingDamage, originalDamage * 1.5f));
    }

    private float calculateArmorReduction(float damage, float armor, float toughness) {
        // Fórmula de reducción de armadura de Minecraft
        float f = 2.0f + toughness / 4.0f;
        float g = Math.min(armor - damage / f, armor * 0.2f);
        return damage * (Math.max(g, 0.0f) / 25.0f);
    }
}