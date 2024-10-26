package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.GolemEntity;
import com.pixelfabric.entity.custom.SkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SkullModel extends GeoModel<SkullEntity> {
    @Override
    public Identifier getModelResource(SkullEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/flying_skull.geo.json");
    }

    @Override
    public Identifier getTextureResource(SkullEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/flying_skull.png");
    }

    @Override
    public Identifier getAnimationResource(SkullEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/flying_skull.animation.json");
    }

    @Override
    public void setCustomAnimations(SkullEntity animatable, long instanceId, AnimationState<SkullEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("h_head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
