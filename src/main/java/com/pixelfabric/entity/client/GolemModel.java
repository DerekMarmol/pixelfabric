package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.GolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GolemModel extends GeoModel<GolemEntity> {
    @Override
    public Identifier getModelResource(GolemEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/golem.geo.json");
    }

    @Override
    public Identifier getTextureResource(GolemEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/golem.png");
    }

    @Override
    public Identifier getAnimationResource(GolemEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/golem.animation.json");
    }

    @Override
    public void setCustomAnimations(GolemEntity animatable, long instanceId, AnimationState<GolemEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("bone5");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
