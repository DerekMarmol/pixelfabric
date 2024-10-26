package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Octana_ExplodeEntity;
import com.pixelfabric.entity.custom.PumpkinFiendeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PumpkinFiendeRenderer extends GeoEntityRenderer<PumpkinFiendeEntity> {

    public PumpkinFiendeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PumpkinFiendeModel());
    }

    @Override
    public Identifier getTextureLocation(PumpkinFiendeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/pumpkinmonster.png");
    }

    @Override
    public void render(PumpkinFiendeEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
