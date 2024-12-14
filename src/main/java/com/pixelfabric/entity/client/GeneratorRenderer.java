package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Candle_SwordEntity;
import com.pixelfabric.entity.custom.GeneratorEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeneratorRenderer extends GeoEntityRenderer<GeneratorEntity> {

    public GeneratorRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GeneratorModel());
    }

    @Override
    public Identifier getTextureLocation(GeneratorEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/generator.png");
    }

    @Override
    public void render(GeneratorEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}