package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.WildfireEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WildfireModel extends GeoModel<WildfireEntity> {
    @Override
    public Identifier getModelResource(WildfireEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/wildfire.geo.json");
    }

    @Override
    public Identifier getTextureResource(WildfireEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/wildfire.png");
    }

    @Override
    public Identifier getAnimationResource(WildfireEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/wildfire.animation.json");
    }

    @Override
    public void setCustomAnimations(WildfireEntity animatable, long instanceId, AnimationState<WildfireEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
