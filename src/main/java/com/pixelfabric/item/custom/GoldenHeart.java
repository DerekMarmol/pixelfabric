package com.pixelfabric.item.custom;

import com.pixelfabric.data.Components;
import com.pixelfabric.data.IPlayerHealthData;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

// El nuevo item GoldenHeart
public class GoldenHeart extends Item {
    public GoldenHeart(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!world.isClient) {
            EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);

            if (healthAttribute != null) {
                IPlayerHealthData healthData = Components.PLAYER_HEALTH.get(player);
                int totalHearts = (int)(healthAttribute.getBaseValue() / 2);  // Corazones totales actuales
                int goldenHearts = healthData.getGoldenHearts();

                // Verificamos si aún podemos convertir más corazones a dorados
                if (goldenHearts < totalHearts) {
                    // Aumentamos el contador de corazones dorados
                    healthData.setGoldenHearts(goldenHearts + 1);

                    // Calculamos la nueva vida máxima
                    // Vida base (2 por corazón normal) + 1 extra por cada corazón dorado
                    double newMaxHealth = (totalHearts * 2) + healthData.getGoldenHearts();

                    // Actualizamos la vida máxima
                    healthAttribute.setBaseValue(newMaxHealth);

                    // Curamos al jugador proporcionalmente
                    float healthPercentage = player.getHealth() / player.getMaxHealth();
                    player.setHealth(player.getMaxHealth() * healthPercentage);

                    // Efectos visuales y de sonido
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    ((ServerWorld) world).spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                            player.getX(), player.getY() + 1, player.getZ(),
                            20,
                            0.5D,
                            0.5D,
                            0.5D,
                            0.1D
                    );

                    // Consumimos el item si no está en modo creativo
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }

                    return TypedActionResult.success(itemStack);
                }
            }
        }

        return TypedActionResult.pass(itemStack);
    }
}
