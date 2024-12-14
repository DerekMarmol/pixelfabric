package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.BarnacleEntity;
import com.pixelfabric.entity.custom.CandikEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CandikModel extends GeoModel<CandikEntity> {
    @Override
    public Identifier getModelResource(CandikEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/candik.geo.json");
    }

    @Override
    public Identifier getTextureResource(CandikEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/qct_candik.png");
    }

    @Override
    public Identifier getAnimationResource(CandikEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/candik.animation.json");
    }

    @Override
    public void setCustomAnimations(CandikEntity animatable, long instanceId, AnimationState<CandikEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
