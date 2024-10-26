package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Octana_ExplodeEntity;
import com.pixelfabric.entity.custom.PumpkinFiendeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PumpkinFiendeModel extends GeoModel<PumpkinFiendeEntity> {
    @Override
    public Identifier getModelResource(PumpkinFiendeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/pumpkin_monster.geo.json");
    }

    @Override
    public Identifier getTextureResource(PumpkinFiendeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/pumpkinmonster.png");
    }

    @Override
    public Identifier getAnimationResource(PumpkinFiendeEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/pumpkin.animation.json");
    }

    @Override
    public void setCustomAnimations(PumpkinFiendeEntity animatable, long instanceId, AnimationState<PumpkinFiendeEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }

}
