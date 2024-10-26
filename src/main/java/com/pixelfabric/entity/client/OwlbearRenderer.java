package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.OwlbearEntity;
import com.pixelfabric.entity.custom.PumpkinFiendeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OwlbearRenderer extends GeoEntityRenderer<OwlbearEntity> {
    public OwlbearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OwlbearModel());
    }

    @Override
    public Identifier getTextureLocation(OwlbearEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/owlbear.png");
    }

    @Override
    public void render(OwlbearEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
