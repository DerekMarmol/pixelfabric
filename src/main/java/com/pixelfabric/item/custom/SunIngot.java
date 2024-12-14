package com.pixelfabric.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SunIngot extends Item {
    private static final int DAMAGE_INTERVAL = 20; // Ticks (1 segundo)
    private static final float DAMAGE_AMOUNT = 1.0f; // Medio corazón de daño

    public SunIngot(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof PlayerEntity player)) {
            return;
        }

        // Solo aplicar daño cada DAMAGE_INTERVAL ticks
        if (world.getTime() % DAMAGE_INTERVAL == 0) {
            // Verificar si el jugador no está en modo creativo
            if (!player.isCreative()) {
                // Aplicar el daño por fuego
                player.damage(player.getDamageSources().onFire(), DAMAGE_AMOUNT);

                // Efectos visuales de fuego (opcional)
                player.setOnFireFor(1); // Prende al jugador por 1 segundo

                // Emitir partículas de fuego (opcional)
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            ParticleTypes.FLAME,
                            player.getX(),
                            player.getY() + 1,
                            player.getZ(),
                            5,
                            0.2,
                            0.2,
                            0.2,
                            0.02
                    );
                }
            }
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false; // Hace que el item brille como los items encantados
    }

    // Opcional: Agregar descripción al item
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("§c¡Cuidado! ¡Quema al tocarlo!")
                .formatted(Formatting.RED));
    }
}