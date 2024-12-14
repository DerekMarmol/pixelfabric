package com.pixelfabric.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class SkullEntity extends FlyingEntity implements GeoEntity {
    private static final TrackedData<Boolean> ATTACHED = DataTracker.registerData(SkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> SPACE_COUNTER = DataTracker.registerData(SkullEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final int MAX_SPACE_COUNTER = 35;
    private static final int EXPLOSION_TIME = 170; // 8.5 segundos
    private static final double LAUNCH_DISTANCE = 8.0; // Distancia a la que será lanzada (bloques)
    private static final double LAUNCH_SPEED = 1.2; // Velocidad de lanzamiento

    private int explosionTimer;
    private PlayerEntity attachedPlayer;
    private boolean hasExploded = false;
    private boolean isLaunched = false;
    private int launchTicks = 0;
    private static final int EXPLOSION_DELAY = 15; // Ticks antes de explotar después del lanzamiento

    public SkullEntity(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
        this.explosionTimer = EXPLOSION_TIME;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        // Invulnerable mientras está pegada o siendo lanzada
        return this.isAttached() || this.isLaunched || super.isInvulnerableTo(damageSource);
    }

    public static DefaultAttributeContainer.Builder createSkullAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.8D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 1.2D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(true);
        birdNavigation.setCanSwim(true);
        return birdNavigation;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACHED, false);
        this.dataTracker.startTracking(SPACE_COUNTER, 0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new CustomFlyGoal(this, 1.2D));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            if (this.isLaunched) {
                this.launchTicks++;
                if (this.launchTicks >= EXPLOSION_DELAY) {
                    this.explode();
                }
                return;
            }

            if (!this.isAttached()) {
                PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 2.0D);
                if (nearestPlayer != null && !nearestPlayer.isCreative()) {
                    this.setAttached(true);
                    this.attachedPlayer = nearestPlayer;
                    this.explosionTimer = EXPLOSION_TIME;
                    this.setSpaceCounter(0);
                    this.hasExploded = false;
                }
            } else if (this.attachedPlayer != null && this.attachedPlayer.isAlive()) {
                this.setPosition(this.attachedPlayer.getX(), this.attachedPlayer.getY() + 2, this.attachedPlayer.getZ());
                this.setVelocity(0, 0, 0);

                if (this.explosionTimer > 0) {
                    this.explosionTimer--;

                    if (this.explosionTimer % 20 == 0) {
                        sendWarningMessage();
                    }
                }

                if (this.explosionTimer <= 0 && !this.hasExploded) {
                    this.explode();
                    this.hasExploded = true;
                }
            } else {
                resetState();
            }
        }
    }

    private void sendWarningMessage() {
        if (this.attachedPlayer instanceof ServerPlayerEntity serverPlayer) {
            float progress = (float)this.getSpaceCounter() / MAX_SPACE_COUNTER;
            int clicksLeft = MAX_SPACE_COUNTER - this.getSpaceCounter();
            double secondsLeft = this.explosionTimer / 20.0;

            int filledBars = (int)(progress * 10);
            String progressBar = "§a" + "■".repeat(filledBars) +
                    "§7" + "■".repeat(10 - filledBars);

            Text message = Text.literal(String.format(
                    "§c¡CALAVERA EXPLOSIVA! §f%d clicks restantes §7(%.1f s) §8[%s§8]",
                    clicksLeft,
                    secondsLeft,
                    progressBar
            ));

            serverPlayer.sendMessage(message, true);
        }
    }

    public void handleSpacePress() {
        if (this.isAttached() && !this.hasExploded) {
            int counter = this.getSpaceCounter() + 1;
            this.setSpaceCounter(counter);

            if (counter >= MAX_SPACE_COUNTER) {
                launchAndExplode();
            }
        }
    }

    private void launchAndExplode() {
        if (this.attachedPlayer != null) {
            // Obtener la dirección hacia donde mira el jugador
            Vec3d lookVec = this.attachedPlayer.getRotationVector();

            // Calcular la posición objetivo basada en la dirección del jugador
            double targetX = this.attachedPlayer.getX() + lookVec.x * LAUNCH_DISTANCE;
            double targetY = this.attachedPlayer.getY() + lookVec.y * LAUNCH_DISTANCE + 1.0; // +1.0 para compensar la gravedad
            double targetZ = this.attachedPlayer.getZ() + lookVec.z * LAUNCH_DISTANCE;

            // Calcular el vector de velocidad
            Vec3d velocity = new Vec3d(
                    lookVec.x * LAUNCH_SPEED,
                    lookVec.y * LAUNCH_SPEED,
                    lookVec.z * LAUNCH_SPEED
            );

            // Aplicar el lanzamiento
            this.setAttached(false);
            this.setVelocity(velocity);
            this.isLaunched = true;
            this.launchTicks = 0;

            // Notificar al jugador
            if (this.attachedPlayer instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.literal("§a¡Has lanzado la calavera explosiva!"), true);
            }
            this.attachedPlayer = null;
        }
    }

    private void resetState() {
        if (!this.isLaunched) {
            this.setAttached(false);
            this.explosionTimer = EXPLOSION_TIME;
            this.setSpaceCounter(0);
            this.hasExploded = false;
            this.attachedPlayer = null;
        }
    }

    private void explode() {
        if (!this.getWorld().isClient && !this.hasExploded) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 3.0F, World.ExplosionSourceType.MOB);
            this.remove(RemovalReason.KILLED);
        }
    }

    public boolean isAttached() {
        return this.dataTracker.get(ATTACHED);
    }

    public void setAttached(boolean attached) {
        this.dataTracker.set(ATTACHED, attached);
    }

    public int getSpaceCounter() {
        return this.dataTracker.get(SPACE_COUNTER);
    }

    public void setSpaceCounter(int count) {
        this.dataTracker.set(SPACE_COUNTER, count);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<SkullEntity> state) {
        if (this.isAttached()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.skull.idle", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.skull.walk", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setAttached(nbt.getBoolean("Attached"));
        this.setSpaceCounter(nbt.getInt("SpaceCounter"));
        this.explosionTimer = nbt.getInt("ExplosionTimer");
        this.hasExploded = nbt.getBoolean("HasExploded");
        this.isLaunched = nbt.getBoolean("IsLaunched");
        this.launchTicks = nbt.getInt("LaunchTicks");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Attached", this.isAttached());
        nbt.putInt("SpaceCounter", this.getSpaceCounter());
        nbt.putInt("ExplosionTimer", this.explosionTimer);
        nbt.putBoolean("HasExploded", this.hasExploded);
        nbt.putBoolean("IsLaunched", this.isLaunched);
        nbt.putInt("LaunchTicks", this.launchTicks);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public class CustomFlyGoal extends Goal {
        private final SkullEntity skull;
        private final double speed;

        public CustomFlyGoal(SkullEntity skull, double speed) {
            this.skull = skull;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !skull.isLaunched && !skull.isAttached() &&
                    skull.getWorld().getClosestPlayer(skull, 20.0D) != null;
        }

        @Override
        public void tick() {
            if (skull.isLaunched || skull.isAttached()) return;

            PlayerEntity target = skull.getWorld().getClosestPlayer(skull, 20.0D);
            if (target != null) {
                double deltaX = target.getX() - skull.getX();
                double deltaY = target.getY() - skull.getY();
                double deltaZ = target.getZ() - skull.getZ();
                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                if (distance >= 1E-7D) {
                    float angle = (float) (MathHelper.atan2(deltaZ, deltaX) * 57.2957763671875D) - 90.0F;
                    skull.setYaw(angle);
                    skull.bodyYaw = skull.getYaw();

                    double speedFactor = speed * skull.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                    skull.setVelocity(skull.getVelocity().add(
                            deltaX / distance * speedFactor * 0.05D,
                            deltaY * speedFactor * 0.05D,
                            deltaZ / distance * speedFactor * 0.05D
                    ));
                }
            }
        }
    }
}