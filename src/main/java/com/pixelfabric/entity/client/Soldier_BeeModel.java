package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Soldier_BeeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class Soldier_BeeModel extends GeoModel<Soldier_BeeEntity> {

    @Override
    public Identifier getModelResource(Soldier_BeeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/soldier_bee.geo.json");
    }

    @Override
    public Identifier getTextureResource(Soldier_BeeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/soldier_bee.png");
    }

    @Override
    public Identifier getAnimationResource(Soldier_BeeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/soldier_bee.animation.json");
    }

    @Override
    public void setCustomAnimations(Soldier_BeeEntity animatable, long instanceId, AnimationState<Soldier_BeeEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("bone4");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
