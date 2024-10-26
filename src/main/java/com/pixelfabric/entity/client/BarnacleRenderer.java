package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.BarnacleEntity;
import com.pixelfabric.entity.custom.Candle_SwordEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BarnacleRenderer extends GeoEntityRenderer<BarnacleEntity> {

    public BarnacleRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BarnacleModel());
    }

    @Override
    public Identifier getTextureLocation(BarnacleEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/barnacle.png");
    }

    @Override
    public void render(BarnacleEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
