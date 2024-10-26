package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.BarnacleEntity;
import com.pixelfabric.entity.custom.Candle_SwordEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BarnacleModel extends GeoModel<BarnacleEntity> {
    @Override
    public Identifier getModelResource(BarnacleEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/barnacle.geo.json");
    }

    @Override
    public Identifier getTextureResource(BarnacleEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/barnacle.png");
    }

    @Override
    public Identifier getAnimationResource(BarnacleEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/barnacle.animation.json");
    }

    @Override
    public void setCustomAnimations(BarnacleEntity animatable, long instanceId, AnimationState<BarnacleEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("h_all");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
