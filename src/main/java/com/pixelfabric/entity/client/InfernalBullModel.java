package com.pixelfabric.entity.client;

import com.pixelfabric.entity.custom.InfernalBullEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class InfernalBullModel extends GeoModel<InfernalBullEntity> {
    @Override
    public Identifier getModelResource(InfernalBullEntity entity) {
        return new Identifier("pixelfabric", "geo/infernal_bull_ultimo.geo.json");
    }

    @Override
    public Identifier getTextureResource(InfernalBullEntity entity) {
        return new Identifier("pixelfabric", "textures/entity/" + entity.getTexture() + ".png");
    }

    @Override
    public Identifier getAnimationResource(InfernalBullEntity entity) {
        return new Identifier("pixelfabric", "animations/infernal_bull_ultimo.animation.json");
    }
}