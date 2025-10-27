package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.ToxinSpiderEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ToxinSpiderRenderer extends GeoEntityRenderer<ToxinSpiderEntity> {
    public ToxinSpiderRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ToxinSpiderModel());
        this.shadowRadius = 2.0F; // Sombra más grande para araña grande
    }

    @Override
    public Identifier getTextureLocation(ToxinSpiderEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/toxin_spider.png");
    }

    @Override
    public void render(ToxinSpiderEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}