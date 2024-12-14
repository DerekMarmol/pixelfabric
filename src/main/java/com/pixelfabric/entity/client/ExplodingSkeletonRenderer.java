package com.pixelfabric.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

public class ExplodingSkeletonRenderer extends SkeletonEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("pixelfabric", "textures/entity/explosive_skeleton.png");

    public ExplodingSkeletonRenderer(EntityRendererFactory.Context ctx) {

        super(ctx);
    }

    @Override
    public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
        return TEXTURE;
    }
}