package com.pixelfabric.entity.client;

import com.pixelfabric.entity.custom.InfernalBullEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class InfernalBullRenderer extends GeoEntityRenderer<InfernalBullEntity> {
    public InfernalBullRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new InfernalBullModel());

        // Ajusta la escala si es necesario
        this.shadowRadius = 0.5f; // Radio de la sombra
    }

    @Override
    protected float getDeathMaxRotation(InfernalBullEntity entityLivingBaseIn) {
        return 0.0F; // Evita que la entidad gire al morir
    }
}