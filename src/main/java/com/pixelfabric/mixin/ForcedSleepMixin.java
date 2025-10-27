package com.pixelfabric.mixin;

import com.pixelfabric.commands.ForcedSleepMechanic;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ForcedSleepMixin {

    private static final String NIGHTS_WITHOUT_SLEEP_KEY = "nightsWithoutSleep";
    private static final String LAST_SLEEP_TIME_KEY = "lastSleepTime";
    private static final int TICKS_PER_NIGHT = 24000; // 20 minutos en ticks

    @Inject(method = "tick", at = @At("HEAD"))
    private void trackSleepDeprivation(CallbackInfo ci) {
        if (!ForcedSleepMechanic.isForcedSleepActive()) {
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

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        NbtCompound playerData = serverPlayer.writeNbt(new NbtCompound());

        long worldTime = player.getWorld().getTimeOfDay();
        long dayTime = worldTime % 24000;

        // Obtener datos del jugador
        int nightsWithoutSleep = playerData.getInt(NIGHTS_WITHOUT_SLEEP_KEY);
        long lastSleepTime = playerData.getLong(LAST_SLEEP_TIME_KEY);

        // Si es la primera vez, inicializar
        if (lastSleepTime == 0) {
            lastSleepTime = worldTime;
            playerData.putLong(LAST_SLEEP_TIME_KEY, lastSleepTime);
            serverPlayer.readNbt(playerData);
        }

        // Verificar si es de noche (13000-23000 ticks del día)
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;

        // Calcular cuántas noches han pasado desde la última vez que durmió
        long timeSinceLastSleep = worldTime - lastSleepTime;
        int calculatedNightsWithoutSleep = (int) Math.max(0, timeSinceLastSleep / TICKS_PER_NIGHT);

        // SIEMPRE actualizar al valor calculado (esto arregla el problema con /time add)
        if (calculatedNightsWithoutSleep != nightsWithoutSleep) {
            nightsWithoutSleep = calculatedNightsWithoutSleep;
            playerData.putInt(NIGHTS_WITHOUT_SLEEP_KEY, nightsWithoutSleep);
            serverPlayer.readNbt(playerData);

            // Debug: notificar cuando cambie el contador
            if (nightsWithoutSleep > 0 && serverPlayer.age % 100 == 0) {
                serverPlayer.sendMessage(
                        Text.literal("§7[Debug] Noches sin dormir: " + nightsWithoutSleep),
                        true
                );
            }
        }

        // Solo aplicar efectos cada 20 ticks (1 segundo) para evitar spam
        if (serverPlayer.age % 20 == 0) {
            applySleepDeprivationEffects(serverPlayer, nightsWithoutSleep, isNight);
        }
    }

    @Inject(method = "wakeUp(ZZ)V", at = @At("HEAD"))
    private void onPlayerWakeUp(boolean bl, boolean updateSleepingPlayers, CallbackInfo ci) {
        if (!ForcedSleepMechanic.isForcedSleepActive()) {
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player.getWorld().isClient) {
            return;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        NbtCompound playerData = serverPlayer.writeNbt(new NbtCompound());

        // Resetear contador al dormir
        playerData.putInt(NIGHTS_WITHOUT_SLEEP_KEY, 0);
        playerData.putLong(LAST_SLEEP_TIME_KEY, player.getWorld().getTimeOfDay());
        serverPlayer.readNbt(playerData);

        // Mensaje de alivio
        serverPlayer.sendMessage(
                Text.literal("§a¡Te sientes descansado y renovado!"),
                true
        );
    }

    private void applySleepDeprivationEffects(ServerPlayerEntity player, int nightsWithoutSleep, boolean isNight) {
        // Efectos progresivos según las noches sin dormir

        if (nightsWithoutSleep >= 3) {
            // 3+ noches: Náusea leve
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, true, false));

            if (player.age % 200 == 0) { // Cada 10 segundos
                player.sendMessage(
                        Text.literal("§7Te sientes mareado por la falta de sueño..."),
                        true
                );
            }
        }

        if (nightsWithoutSleep >= 5) {
            // 5+ noches: Debilidad y náusea más fuerte
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 120, 1, true, false));

            if (player.age % 400 == 0) { // Cada 20 segundos
                player.sendMessage(
                        Text.literal("§6¡Tu cuerpo se siente extremadamente cansado!"),
                        true
                );
            }
        }

        if (nightsWithoutSleep >= 7) {
            // 7+ noches: Efectos severos
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 120, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 100, 0, true, false));

            // Alucinaciones visuales (ceguera ocasional)
            if (player.getRandom().nextFloat() < 0.02f && isNight) { // 2% de probabilidad por segundo en la noche
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0, true, false));

                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_ENDERMAN_STARE,
                        SoundCategory.AMBIENT,
                        0.5f,
                        0.8f
                );

                player.sendMessage(
                        Text.literal("§4¡Las sombras danzan ante tus ojos!"),
                        true
                );
            }

            if (player.age % 600 == 0) { // Cada 30 segundos
                player.sendMessage(
                        Text.literal("§4¡Necesitas dormir urgentemente o colapsarás!"),
                        false
                );
            }
        }

        if (nightsWithoutSleep >= 10) {
            // 10+ noches: Efectos críticos
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 140, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 120, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 120, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 100, 0, true, false));

            // Daño ocasional por agotamiento extremo
            if (player.getRandom().nextFloat() < 0.01f) { // 1% probabilidad por segundo
                player.damage(player.getDamageSources().magic(), 1.0f);

                player.sendMessage(
                        Text.literal("§4¡Tu cuerpo se está desmoronando por la falta de sueño!"),
                        true
                );

                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_PLAYER_HURT,
                        SoundCategory.PLAYERS,
                        0.8f,
                        0.7f
                );
            }
        }
    }
}