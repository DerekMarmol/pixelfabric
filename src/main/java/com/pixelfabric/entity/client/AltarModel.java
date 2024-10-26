package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.AltarEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AltarModel extends GeoModel<AltarEntity> {

    @Override
    public Identifier getModelResource(AltarEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/dungeon_spawn.geo.json");
    }

    @Override
    public Identifier getTextureResource(AltarEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/dungeon_spawn.png");
    }

    @Override
    public Identifier getAnimationResource(AltarEntity animatable) {
        return null;
    }
}
