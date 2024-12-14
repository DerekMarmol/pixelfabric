package com.pixelfabric.item.custom;

import com.pixelfabric.data.Components;
import com.pixelfabric.data.IPlayerHealthData;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

public class HeartContainer extends Item {
    public HeartContainer(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!world.isClient) {
            EntityAttributeInstance healthAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);

            if (healthAttribute != null) {
                // Obtenemos el componente de datos del jugador
                IPlayerHealthData healthData = Components.PLAYER_HEALTH.get(player);
                int currentExtraHearts = healthData.getExtraHearts();

                // Aumentamos los corazones extra
                healthData.setExtraHearts(currentExtraHearts + 2);

                // Actualizamos la vida máxima
                healthAttribute.setBaseValue(20.0D + currentExtraHearts + 2);

                // Curamos al jugador
                player.setHealth(player.getMaxHealth());

                // Efectos visuales y de sonido
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);

                ((ServerWorld) world).spawnParticles(ParticleTypes.HEART,
                        player.getX(), player.getY() + 1, player.getZ(),
                        10,
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

        return TypedActionResult.pass(itemStack);
    }
}