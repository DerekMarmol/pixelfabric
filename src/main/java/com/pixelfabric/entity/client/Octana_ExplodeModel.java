package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Octana_ExplodeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class Octana_ExplodeModel extends GeoModel<Octana_ExplodeEntity> {

    @Override
    public Identifier getModelResource(Octana_ExplodeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/spiderboss.geo.json");
    }

    @Override
    public Identifier getTextureResource(Octana_ExplodeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/octanaexploder.png");
    }

    @Override
    public Identifier getAnimationResource(Octana_ExplodeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/spiderboss.animation.json");
    }

    @Override
    public void setCustomAnimations(Octana_ExplodeEntity animatable, long instanceId, AnimationState<Octana_ExplodeEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
