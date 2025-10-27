package com.pixelfabric.entity.client;

import com.pixelfabric.entity.AngryChickenEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class AngryChickenRenderer extends MobEntityRenderer<AngryChickenEntity, ChickenEntityModel<AngryChickenEntity>> {

    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/entity/chicken.png");

    public AngryChickenRenderer(EntityRendererFactory.Context context) {
        super(context, new ChickenEntityModel<>(context.getPart(EntityModelLayers.CHICKEN)), 0.3f);
    }

    @Override
    public Identifier getTexture(AngryChickenEntity entity) {
        return TEXTURE;
    }
}
