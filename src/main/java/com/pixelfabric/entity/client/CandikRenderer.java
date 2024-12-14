package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.CandikEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CandikRenderer extends GeoEntityRenderer<CandikEntity> {
    public CandikRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CandikModel());
    }

    @Override
    public Identifier getTextureLocation(CandikEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/qct_candik.png");
    }

    @Override
    public void render(CandikEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
