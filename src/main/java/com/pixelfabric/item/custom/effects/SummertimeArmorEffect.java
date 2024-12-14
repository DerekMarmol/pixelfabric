package com.pixelfabric.item.custom.effects;

import net.minecraft.entity.player.PlayerEntity;

public class SummertimeArmorEffect implements ArmorEffect {

    @Override
    public void applyEffect(PlayerEntity player) {
        if (player.isSubmergedInWater()) {
            // Restablece la respiración bajo el agua si el jugador está usando la armadura completa y está sumergido
            player.setAir(Math.min(player.getAir() + 1, player.getMaxAir()));
        }
    }

    @Override
    public void removeEffect(PlayerEntity player) {
        // No es necesario hacer nada al quitar el efecto para esta armadura
    }
}
