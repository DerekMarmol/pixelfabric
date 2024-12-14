package com.pixelfabric.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class MinerZombieRenderer extends ZombieEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("pixelfabric", "textures/entity/miner_zombie.png");

    public MinerZombieRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ZombieEntity zombieEntity) {
        return TEXTURE;
    }
}