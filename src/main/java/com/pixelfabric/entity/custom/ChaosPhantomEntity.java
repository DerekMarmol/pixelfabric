package com.pixelfabric.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil; // <--- Esta es la línea que falta

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChaosPhantomEntity extends PhantomEntity implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ChaosPhantomEntity(EntityType<? extends PhantomEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createChaosPhantomAttributes() {
        return PhantomEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean attacked = super.tryAttack(target);

        if (attacked && target instanceof PlayerEntity player) {
            shufflePlayerInventory(player);
        }

        return attacked;
    }

    private void shufflePlayerInventory(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> itemsToShuffle = new ArrayList<>();

        // Recolectamos todos los items (excepto armor y offhand)
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);
            if (!stack.isEmpty()) {
                itemsToShuffle.add(stack.copy());
                inventory.main.set(i, ItemStack.EMPTY);
            }
        }

        // Mezclamos los items
        Collections.shuffle(itemsToShuffle);

        // Recolocamos los items en posiciones aleatorias
        int currentSlot = 0;
        for (ItemStack stack : itemsToShuffle) {
            while (currentSlot < inventory.main.size() && !inventory.main.get(currentSlot).isEmpty()) {
                currentSlot++;
            }
            if (currentSlot < inventory.main.size()) {
                inventory.main.set(currentSlot, stack);
                currentSlot++;
            }
        }

        // Actualizamos el inventario
        player.playerScreenHandler.sendContentUpdates();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> animationState) {
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().thenLoop("animation.chaosphantom.move"));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}