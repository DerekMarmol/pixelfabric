package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.CandikEntity;
import com.pixelfabric.entity.custom.Lava_SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class Lava_SquidModel extends GeoModel<Lava_SquidEntity> {

    @Override
    public Identifier getModelResource(Lava_SquidEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/lava_quid.geo.json");
    }

    @Override
    public Identifier getTextureResource(Lava_SquidEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/lava_quid.png");
    }

    @Override
    public Identifier getAnimationResource(Lava_SquidEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/lava_quid.animation.json");
    }

    @Override
    public void setCustomAnimations(Lava_SquidEntity animatable, long instanceId, AnimationState<Lava_SquidEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("forhead");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
