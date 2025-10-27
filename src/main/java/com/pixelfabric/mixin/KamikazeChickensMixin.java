package com.pixelfabric.mixin;

import com.pixelfabric.commands.KamikazeChickensMechanic;
import com.pixelfabric.entity.AngryChickenEntity;
import com.pixelfabric.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class KamikazeChickensMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void onChickenDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Verificar que la entidad sea un pollo
        if (!((Object) this instanceof ChickenEntity chicken)) {
            return;
        }

        // Solo si la mecánica está activa
        if (!KamikazeChickensMechanic.isKamikazeChickensActive()) {
            return;
        }

        // Solo en el servidor
        if (chicken.getWorld().isClient) {
            return;
        }

        // Solo si el atacante es un jugador
        if (!(source.getAttacker() instanceof PlayerEntity attacker)) {
            return;
        }

        // No afectar jugadores en creativo o espectador
        if (attacker.isCreative() || attacker.isSpectator()) {
            return;
        }

        ServerWorld world = (ServerWorld) chicken.getWorld();

        // Crear el nuevo pollo hostil
        AngryChickenEntity angryChicken = new AngryChickenEntity(ModEntities.ANGRY_CHICKEN, world);
        angryChicken.refreshPositionAndAngles(chicken.getX(), chicken.getY(), chicken.getZ(), chicken.getYaw(), chicken.getPitch());
        angryChicken.setTarget(attacker);

        // Spawn en el mundo
        world.spawnEntity(angryChicken);

        // Eliminar el pollo original
        chicken.discard();
    }
}
