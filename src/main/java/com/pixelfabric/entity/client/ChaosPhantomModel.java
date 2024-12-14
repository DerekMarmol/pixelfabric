package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.ChaosPhantomEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ChaosPhantomModel extends GeoModel<ChaosPhantomEntity> {
    @Override
    public Identifier getModelResource(ChaosPhantomEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/chaosphantom.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChaosPhantomEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/chaosphantom.png");
    }

    @Override
    public Identifier getAnimationResource(ChaosPhantomEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/chaosphantom.animation.json");
    }
}