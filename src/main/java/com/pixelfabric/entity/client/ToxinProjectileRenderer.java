package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.ToxinProjectileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ToxinProjectileRenderer extends GeoEntityRenderer<ToxinProjectileEntity> {
    public ToxinProjectileRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ToxinProjectileModel());
        this.shadowRadius = 0.25F; // Sombra pequeña para el proyectil
    }

    @Override
    public Identifier getTextureLocation(ToxinProjectileEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/toxin_projectile.png");
    }

    @Override
    public void render(ToxinProjectileEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}