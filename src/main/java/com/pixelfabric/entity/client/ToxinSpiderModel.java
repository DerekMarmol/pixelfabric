package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.ToxinSpiderEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ToxinSpiderModel extends GeoModel<ToxinSpiderEntity> {
    @Override
    public Identifier getModelResource(ToxinSpiderEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/toxin_spider.geo.json");
    }

    @Override
    public Identifier getTextureResource(ToxinSpiderEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/toxin_spider.png");
    }

    @Override
    public Identifier getAnimationResource(ToxinSpiderEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/toxin_spider.animation.json");
    }

    @Override
    public void setCustomAnimations(ToxinSpiderEntity animatable, long instanceId, AnimationState<ToxinSpiderEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}