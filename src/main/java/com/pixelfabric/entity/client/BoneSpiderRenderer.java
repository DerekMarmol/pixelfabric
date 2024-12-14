package com.pixelfabric.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

public class BoneSpiderRenderer extends SpiderEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("pixelfabric", "textures/entity/bone_spider.png");

    public BoneSpiderRenderer(EntityRendererFactory.Context ctx) {

        super(ctx);
    }

    @Override
    public Identifier getTexture(SpiderEntity spiderEntity) {
        return TEXTURE;
    }
}