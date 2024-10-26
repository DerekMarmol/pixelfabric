package com.pixelfabric.entity.client;


import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.WildfireEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WildfireRenderer extends GeoEntityRenderer<WildfireEntity> {

    public WildfireRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WildfireModel());
    }

    @Override
    public Identifier getTextureLocation(WildfireEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/wildfire.png");
    }

    @Override
    public void render(WildfireEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()){
            poseStack.scale(0.4f,0.4f,0.4f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
