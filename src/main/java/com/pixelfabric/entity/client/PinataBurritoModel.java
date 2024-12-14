package com.pixelfabric.entity.client;

import com.pixelfabric.entity.custom.PinataBurritoEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PinataBurritoModel extends GeoModel<PinataBurritoEntity> {
    @Override
    public Identifier getModelResource(PinataBurritoEntity entity) {
        return new Identifier("pixelfabric", "geo/burritopinata_em.geo.json");
    }

    @Override
    public Identifier getTextureResource(PinataBurritoEntity entity) {
        return new Identifier("pixelfabric", "textures/entity/burrito_em.png");
    }

    @Override
    public Identifier getAnimationResource(PinataBurritoEntity entity) {
        return new Identifier("pixelfabric", "animations/burrito_em.animation.json");
    }
}
