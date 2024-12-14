package com.pixelfabric.mixin;

import com.pixelfabric.item.custom.HeartTotemItem;
import com.pixelfabric.item.custom.RegenerationTotemItem;
import com.pixelfabric.item.custom.InventoryTotemItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(
            method = "onEntityStatus",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onHandleEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        // SECCIÓN 1: VERIFICACIONES INICIALES
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;

        // Verificar que el mundo está cargado
        if (world == null) {
            return;
        }

        Entity entity = packet.getEntity(world);

        // Verificar que la entidad existe
        if (entity == null) {
            return;
        }

        // SECCIÓN 2: MANEJO DE EFECTOS VISUALES DEL TOTEM
        // El status 35 es el código para efectos de totem
        if (packet.getStatus() == 35) {
            ItemStack totemStack = null;

            // Si la entidad es un jugador, buscamos el totem usado
            if (entity instanceof PlayerEntity player) {
                ItemStack mainHand = player.getMainHandStack();
                ItemStack offHand = player.getOffHandStack();

                // Primero buscamos en las manos
                if (mainHand.getItem() instanceof HeartTotemItem ||
                        mainHand.getItem() instanceof RegenerationTotemItem ||
                        mainHand.getItem() instanceof InventoryTotemItem) {
                    totemStack = mainHand;
                } else if (offHand.getItem() instanceof HeartTotemItem ||
                        offHand.getItem() instanceof RegenerationTotemItem ||
                        offHand.getItem() instanceof InventoryTotemItem) {
                    totemStack = offHand;
                } else {
                    // Si no está en las manos, buscamos el InventoryTotem en el inventario
                    for (int i = 0; i < player.getInventory().size(); i++) {
                        ItemStack stack = player.getInventory().getStack(i);
                        if (stack.getItem() instanceof InventoryTotemItem) {
                            totemStack = stack;
                            break;
                        }
                    }
                }
            }

            // SECCIÓN 3: APLICAR EFECTOS VISUALES
            if (totemStack != null) {
                // Elegir el tipo de partículas según el totem
                DefaultParticleType particleType = ParticleTypes.TOTEM_OF_UNDYING;
                if (totemStack.getItem() instanceof RegenerationTotemItem) {
                    particleType = ParticleTypes.HEART;
                }

                // Generar partículas
                client.particleManager.addEmitter(entity, particleType, 30);

                // Reproducir sonido
                world.playSound(
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.ITEM_TOTEM_USE,
                        SoundCategory.PLAYERS,
                        1.0F,
                        1.0F,
                        false
                );

                // Mostrar el item flotando si es el jugador local
                if (entity == client.player) {
                    client.gameRenderer.showFloatingItem(totemStack);
                }

                // Cancelar el manejo vanilla del efecto
                ci.cancel();
            }
        }
    }
}