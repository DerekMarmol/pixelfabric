package com.pixelfabric.entity.client;

import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

public class MoobloomRenderer extends CowEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("pixelfabric", "textures/entity/moobloom.png");

    public MoobloomRenderer(EntityRendererFactory.Context ctx) {

        super(ctx);
    }

    @Override
    public Identifier getTexture(CowEntity cowEntity) {
        return TEXTURE;
    }
}
