package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Soldier_BeeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Soldier_BeeRenderer extends GeoEntityRenderer<Soldier_BeeEntity> {

    public Soldier_BeeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new Soldier_BeeModel());
    }

    @Override
    public Identifier getTextureLocation(Soldier_BeeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/soldier_bee.png");
    }

    @Override
    public void render(Soldier_BeeEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()){
            poseStack.scale(0.4f,0.4f,0.4f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
