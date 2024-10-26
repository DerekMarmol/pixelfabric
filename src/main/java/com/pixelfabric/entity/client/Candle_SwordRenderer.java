package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Candle_SwordEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Candle_SwordRenderer extends GeoEntityRenderer<Candle_SwordEntity> {

    public Candle_SwordRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new Candle_SwordModel());
    }

    @Override
    public Identifier getTextureLocation(Candle_SwordEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/candlesword.png");
    }

    @Override
    public void render(Candle_SwordEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
