package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.ChaosPhantomEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChaosPhantomRenderer extends GeoEntityRenderer<ChaosPhantomEntity> {
    public ChaosPhantomRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ChaosPhantomModel());
    }

    @Override
    public Identifier getTextureLocation(ChaosPhantomEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/chaosphantom.png");
    }

    @Override
    public void render(ChaosPhantomEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}