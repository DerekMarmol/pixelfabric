package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.ZombieTankEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ZombieTankModel extends GeoModel<ZombieTankEntity> {
    @Override
    public Identifier getModelResource(ZombieTankEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/zombie_tank.geo.json");
    }

    @Override
    public Identifier getTextureResource(ZombieTankEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/zombie_tank.png");
    }

    @Override
    public Identifier getAnimationResource(ZombieTankEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/zombie_tank.animation.json");
    }

    @Override
    public void setCustomAnimations(ZombieTankEntity animatable, long instanceId, AnimationState<ZombieTankEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}