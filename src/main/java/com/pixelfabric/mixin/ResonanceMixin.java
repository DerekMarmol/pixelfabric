package com.pixelfabric.mixin;

import com.pixelfabric.Enchantments.ModEnchantments;
import com.pixelfabric.Enchantments.custom.ResonanceEnchantment;
import com.pixelfabric.commands.TripleMobResistance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ResonanceMixin {
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyResonanceDamage(float amount, DamageSource source) {
        // Verificar si el daño fue causado por una entidad
        if (source.getAttacker() instanceof LivingEntity attacker) {
            // Obtener el item en la mano principal del atacante
            ItemStack weapon = attacker.getMainHandStack();

            // Verificar si la espada tiene el encantamiento de Resonancia
            int resonanceLevel = net.minecraft.enchantment.EnchantmentHelper.getLevel(
                    ModEnchantments.RESONANCE,
                    weapon
            );

            // Solo proceder si el encantamiento está presente
            if (resonanceLevel > 0 && ResonanceEnchantment.INSTANCE != null) {
                // Calcular el multiplicador de daño
                float damageMultiplier = ResonanceEnchantment.INSTANCE.getDamageMultiplier(attacker, resonanceLevel);

                // Modificar el daño
                amount *= damageMultiplier;

                // Añadir efectos visuales y de sonido
                ResonanceEnchantment.INSTANCE.addResonanceEffects(attacker, (LivingEntity)(Object)this, damageMultiplier);
            }
        }

        return amount;
    }
}