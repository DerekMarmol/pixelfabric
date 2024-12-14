package com.pixelfabric.mixin;

import com.pixelfabric.commands.CreeperFlashEnhancement;
import com.pixelfabric.network.CreeperFlashPacket;
import com.pixelfabric.network.FlashEffectPacket;
import com.pixelfabric.sound.ModSounds;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import net.minecraft.util.Identifier;

@Mixin(CreeperEntity.class)
public abstract class CreeperFlashMixin {
    @Inject(method = "explode", at = @At("HEAD"))
    private void onExplode(CallbackInfo ci) {
        if (CreeperFlashEnhancement.isCreeperFlashActive()) {
            CreeperEntity creeper = (CreeperEntity) (Object) this;
            World world = creeper.getWorld();

            List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                    PlayerEntity.class,
                    creeper.getBoundingBox().expand(10.0),
                    player -> !player.isSpectator() && !player.isCreative()
            );

            for (PlayerEntity player : nearbyPlayers) {
                if (player instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

                    // Enviar efecto de flash
                    CreeperFlashPacket.send(serverPlayer);

                    // Reproducir sonido de tinnitus
                    serverPlayer.playSound(
                            Registries.SOUND_EVENT.get(new Identifier("pixelfabric", "tinnitus")),
                            SoundCategory.HOSTILE,
                            1.0F,
                            1.0F
                    );
                }
            }
        }
    }
}