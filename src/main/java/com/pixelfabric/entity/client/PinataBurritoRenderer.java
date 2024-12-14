package com.pixelfabric.entity.client;

import com.pixelfabric.entity.custom.PinataBurritoEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class PinataBurritoRenderer extends GeoEntityRenderer<PinataBurritoEntity> {
    public PinataBurritoRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PinataBurritoModel());
        this.shadowRadius = 0.4f;

        // Agregar capa de renderizado personalizada
        addRenderLayer(new PinataBurritoRenderLayer(this));
    }

    @Override
    protected float getDeathMaxRotation(PinataBurritoEntity entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    public void render(PinataBurritoEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        poseStack.push();
        poseStack.translate(0, 0.01f, 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pop();
    }
}

// Nueva clase para la capa de renderizado personalizada
class PinataBurritoRenderLayer extends GeoRenderLayer<PinataBurritoEntity> {
    public PinataBurritoRenderLayer(GeoEntityRenderer<PinataBurritoEntity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, PinataBurritoEntity animatable, BakedGeoModel bakedModel,
                       RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {
        // Obtener el consumidor de vértices con la textura y sin culling
        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                RenderLayer.getEntityTranslucent(getRenderer().getTextureLocation(animatable))
        );

        // Renderizar el modelo con las propiedades personalizadas
        getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                renderType,
                vertexConsumer,
                partialTick,
                packedLight,
                packedOverlay,
                1f, 1f, 1f, 1f  // Color RGBA
        );
    }
}