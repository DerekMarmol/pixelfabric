package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.ZombieTankEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZombieTankRenderer extends GeoEntityRenderer<ZombieTankEntity> {
    public ZombieTankRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ZombieTankModel());
        this.shadowRadius = 1.2f; // Sombra más grande por ser un tanque
    }

    @Override
    public Identifier getTextureLocation(ZombieTankEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/zombie_tank.png");
    }

    @Override
    public void render(ZombieTankEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        // Hacer el tanque ligeramente más grande para dar sensación de peso
        poseStack.scale(1.1f, 1.1f, 1.1f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected float getDeathMaxRotation(ZombieTankEntity entityLivingBaseIn) {
        return 0.0F; // Evita que gire al morir, como un tanque pesado
    }
}