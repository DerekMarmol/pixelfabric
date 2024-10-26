package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Octana_ExplodeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Octana_ExplodeRenderer extends GeoEntityRenderer<Octana_ExplodeEntity> {
    public Octana_ExplodeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new Octana_ExplodeModel());
    }

    @Override
    public Identifier getTextureLocation(Octana_ExplodeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/octanaexploder.png");
    }

    @Override
    public void render(Octana_ExplodeEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
