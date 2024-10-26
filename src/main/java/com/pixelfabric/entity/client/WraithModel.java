package com.pixelfabric.entity.client;
import com.pixelfabric.PixelFabric;
import com.pixelfabric.entity.custom.WraithEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WraithModel extends GeoModel<WraithEntity> {
    @Override
    public Identifier getModelResource(WraithEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "geo/wraith.geo.json");
    }

    @Override
    public Identifier getTextureResource(WraithEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "textures/entity/wraith.png");
    }

    @Override
    public Identifier getAnimationResource(WraithEntity animatable) {
        return new Identifier(PixelFabric.MOD_ID, "animations/wraith.animation.json");
    }

    @Override
    public void setCustomAnimations(WraithEntity animatable, long instanceId, AnimationState<WraithEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("h_head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
