package com.pixelfabric.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;

public class BoneSpiderEntity extends SpiderEntity {
    private int webPlacementCooldown = 0;

    public BoneSpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createBoneSpiderAttributes() {
        return SpiderEntity.createSpiderAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 45.0D)  // Vida aumentada (normal es 16)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D) // Daño aumentado (normal es 2)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D); // Velocidad aumentada (normal es 0.3)
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isClient) {
            if (webPlacementCooldown > 0) {
                webPlacementCooldown--;
            }

            // Verificar si hay jugadores cercanos
            PlayerEntity nearestPlayer = getWorld().getClosestPlayer(this, 2.0D);
            if (nearestPlayer != null && webPlacementCooldown <= 0) {
                // Obtener la posición del jugador
                BlockPos playerPos = nearestPlayer.getBlockPos();

                // Verificar si el bloque debajo está vacío
                if (getWorld().getBlockState(playerPos).isAir()) {
                    // Colocar telaraña
                    getWorld().setBlockState(playerPos, Blocks.COBWEB.getDefaultState());
                    webPlacementCooldown = 40; // 40 ticks = 2 segundos
                }
            }
        }
    }
}
