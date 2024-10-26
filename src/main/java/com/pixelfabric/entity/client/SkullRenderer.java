package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.GolemEntity;
import com.pixelfabric.entity.custom.SkullEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkullRenderer extends GeoEntityRenderer<SkullEntity> {
    public SkullRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SkullModel());
    }

    @Override
    public Identifier getTextureLocation(SkullEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/flying_skull.png");
    }

    @Override
    public void render(SkullEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
