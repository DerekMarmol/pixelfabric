package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Candle_SwordEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class Candle_SwordModel extends GeoModel<Candle_SwordEntity> {

    @Override
    public Identifier getModelResource(Candle_SwordEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/candlesword.geo.json");
    }

    @Override
    public Identifier getTextureResource(Candle_SwordEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/candlesword.png");
    }

    @Override
    public Identifier getAnimationResource(Candle_SwordEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/candlesword.animation.json");
    }

    @Override
    public void setCustomAnimations(Candle_SwordEntity animatable, long instanceId, AnimationState<Candle_SwordEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
