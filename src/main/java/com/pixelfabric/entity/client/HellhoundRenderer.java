package com.pixelfabric.entity.client;


import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.HellhoundEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HellhoundRenderer extends GeoEntityRenderer<HellhoundEntity> {

    public HellhoundRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HellhoundModel());
    }

    @Override
    public Identifier getTextureLocation(HellhoundEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/hellhound.png");
    }

    @Override
    public void render(HellhoundEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
