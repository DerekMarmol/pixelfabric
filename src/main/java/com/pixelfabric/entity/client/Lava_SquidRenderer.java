package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.CandikEntity;
import com.pixelfabric.entity.custom.Lava_SquidEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Lava_SquidRenderer extends GeoEntityRenderer<Lava_SquidEntity> {
    public Lava_SquidRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new Lava_SquidModel());
    }

    @Override
    public Identifier getTextureLocation(Lava_SquidEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/lava_quid.png");
    }

    @Override
    public void render(Lava_SquidEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
