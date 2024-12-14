package com.pixelfabric.entity.custom;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.minigames.PinataMinigame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PinataBurritoEntity extends PassiveEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Map<UUID, Long> playerHitCooldowns = new HashMap<>();
    private AnimationState currentAnimationState;
    private static final RawAnimation HIT_ANIM = RawAnimation.begin().then("animation.burrito_em.hit_north", Animation.LoopType.PLAY_ONCE);
    private final Map<UUID, Integer> playerHitCount = new HashMap<>();
    private static final int HIT_COOLDOWN = 100; // 7 segundos (20 ticks * 7)
    private BlockPos anchorPoint;

    public PinataBurritoEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.setPersistent();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PassiveEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void initGoals() {
        // La piñata no necesita goals ya que es estática
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            // Verificar si el jugador está usando el palo de piñata
            if (player.getMainHandStack().isOf(ModItems.PINATABAT_EM)) {
                return handlePinataHit(player);
            }
        }
        return false;
    }

    private boolean handlePinataHit(PlayerEntity player) {
        UUID playerId = player.getUuid();
        long currentTime = this.getWorld().getTime();
        long lastHitTime = playerHitCooldowns.getOrDefault(playerId, 0L);

        if (currentTime - lastHitTime < HIT_COOLDOWN) {
            return false;
        }

        // Actualizar cooldown
        playerHitCooldowns.put(playerId, currentTime);

        // Incrementar contador de golpes
        int hits = playerHitCount.getOrDefault(playerId, 0) + 1;
        playerHitCount.put(playerId, hits);

        // Activar animación de golpe
        this.triggerAnim("controller_name", "hit_north");

        if (player instanceof ServerPlayerEntity serverPlayer) {
            PinataMinigame.getInstance().processHit(serverPlayer);
        }

        return true;
    }

    private void triggerHitAnimation() {
        // Trigger the hit_north animation
        this.triggerAnim("controller_name", "hit_north");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller_name", 0, this::predicate)
                .triggerableAnim("hit_north", HIT_ANIM));
    }

    private PlayState predicate(AnimationState event) {
        this.currentAnimationState = event;
        // Si no hay animación activa, mantener el estado idle
        return PlayState.CONTINUE;
    }

    @Override
    public boolean isPushable() {
        return false; // Evitar que sea empujada
    }

    @Override
    protected void pushAway(Entity entity) {
        // Anular el método para evitar ser empujada
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // Métodos para el punto de anclaje
    public void setAnchorPoint(BlockPos pos) {
        this.anchorPoint = pos;
        this.setPos(pos.getX() + 0.5, pos.getY() - 1.5, pos.getZ() + 0.5);
    }

    @Override
    public void tick() {
        super.tick();
        if (anchorPoint != null) {
            // Mantener la posición fija
            this.setPos(anchorPoint.getX() + 0.5, anchorPoint.getY() - 1.5, anchorPoint.getZ() + 0.5);
            this.setVelocity(0, 0, 0);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (anchorPoint != null) {
            nbt.putInt("AnchorX", anchorPoint.getX());
            nbt.putInt("AnchorY", anchorPoint.getY());
            nbt.putInt("AnchorZ", anchorPoint.getZ());
        }
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("AnchorX")) {
            anchorPoint = new BlockPos(
                    nbt.getInt("AnchorX"),
                    nbt.getInt("AnchorY"),
                    nbt.getInt("AnchorZ")
            );
        }
    }
}