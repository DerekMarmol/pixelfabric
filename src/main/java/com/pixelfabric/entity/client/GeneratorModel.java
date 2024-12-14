package com.pixelfabric.entity.client;

import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.Candle_SwordEntity;
import com.pixelfabric.entity.custom.GeneratorEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GeneratorModel extends GeoModel<GeneratorEntity> {

    @Override
    public Identifier getModelResource(GeneratorEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/generator.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeneratorEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/generator.png");
    }

    @Override
    public Identifier getAnimationResource(GeneratorEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/generator.animation.json");
    }

    @Override
    public void setCustomAnimations(GeneratorEntity animatable, long instanceId, AnimationState<GeneratorEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}