package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.OwlbearEntity;
import com.pixelfabric.entity.custom.PumpkinFiendeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OwlbearModel extends GeoModel<OwlbearEntity> {
    @Override
    public Identifier getModelResource(OwlbearEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/owlbear.geo.json");
    }

    @Override
    public Identifier getTextureResource(OwlbearEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/owlbear.png");
    }

    @Override
    public Identifier getAnimationResource(OwlbearEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/owlbear.animation.json");
    }

    @Override
    public void setCustomAnimations(OwlbearEntity animatable, long instanceId, AnimationState<OwlbearEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("hi_head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
