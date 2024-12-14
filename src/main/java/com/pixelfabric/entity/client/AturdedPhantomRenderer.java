package com.pixelfabric.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PhantomEntityRenderer;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

public class AturdedPhantomRenderer extends PhantomEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("pixelfabric", "textures/entity/aturdedphantom.png");

    public AturdedPhantomRenderer(EntityRendererFactory.Context ctx) {

        super(ctx);
    }

    @Override
    public Identifier getTexture(PhantomEntity phantomEntity) {
        return TEXTURE;
    }
}
