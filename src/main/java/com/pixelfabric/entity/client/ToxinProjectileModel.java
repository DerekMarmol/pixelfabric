package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.ToxinProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ToxinProjectileModel extends GeoModel<ToxinProjectileEntity> {
    @Override
    public Identifier getModelResource(ToxinProjectileEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/toxin_projectile.geo.json");
    }

    @Override
    public Identifier getTextureResource(ToxinProjectileEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/toxin_projectile.png");
    }

    @Override
    public Identifier getAnimationResource(ToxinProjectileEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/toxin_projectile.animation.json");
    }
}