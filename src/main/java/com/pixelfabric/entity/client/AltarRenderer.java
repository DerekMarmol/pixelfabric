package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.AltarEntity;
import com.pixelfabric.entity.custom.Candle_SwordEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AltarRenderer extends GeoEntityRenderer<AltarEntity> {

    public AltarRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AltarModel());
    }

    @Override
    public Identifier getTextureLocation(AltarEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/dungeon_spawn.png");
    }

    @Override
    public void render(AltarEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
