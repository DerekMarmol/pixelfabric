package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.HellhoundEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HellhoundModel extends GeoModel<HellhoundEntity> {
    @Override
    public Identifier getModelResource(HellhoundEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/hellhound.geo.json");
    }

    @Override
    public Identifier getTextureResource(HellhoundEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/hellhound.png");
    }

    @Override
    public Identifier getAnimationResource(HellhoundEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/hellhound.animation.json");
    }

    @Override
    public void setCustomAnimations(HellhoundEntity animatable, long instanceId, AnimationState<HellhoundEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("h_head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
