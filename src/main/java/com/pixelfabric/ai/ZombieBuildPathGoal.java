package com.pixelfabric.ai;

import com.pixelfabric.commands.ZombieEnhancementMechanic;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Hand;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;

public class ZombieBuildPathGoal extends Goal {
    private final ZombieEntity zombie;
    private PlayerEntity target;
    private int buildCooldown = 0;
    private static final int BUILD_COOLDOWN_TIME = 5;
    private static final double BUILD_RANGE = 20.0D;
    private static final double HEIGHT_DIFFERENCE_THRESHOLD = 2.0D;
    private boolean hasBlock = true;
    private boolean isJumping = false;
    private BlockPos targetBuildPos = null;
    private int jumpTicks = 0;
    private static final int JUMP_DURATION = 10;
    private boolean hasPlacedBlock = false;

    public ZombieBuildPathGoal(ZombieEntity zombie) {
        this.zombie = zombie;
    }

    private void updateZombieHeldItem() {
        if (ZombieEnhancementMechanic.isZombieEnhancementActive()) {
            if (hasBlock && zombie.getMainHandStack().isEmpty()) {
                zombie.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIRT));
            } else if (!hasBlock) {
                zombie.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void start() {
        updateZombieHeldItem();
        hasPlacedBlock = false;
    }

    @Override
    public boolean canStart() {
        if (!ZombieEnhancementMechanic.isZombieEnhancementActive()) {
            return false;
        }

        if (!(zombie.getWorld() instanceof ServerWorld)) {
            return false;
        }

        this.target = this.zombie.getWorld().getClosestPlayer(
                this.zombie.getX(),
                this.zombie.getY(),
                this.zombie.getZ(),
                BUILD_RANGE,
                true
        );

        return target != null &&
                (target.getY() - zombie.getY() >= HEIGHT_DIFFERENCE_THRESHOLD) &&
                zombie.canSee(target);
    }

    private boolean canBuildAt(BlockPos pos) {
        BlockState blockBelow = zombie.getWorld().getBlockState(pos.down());
        return blockBelow.isSolid() &&
                zombie.getWorld().getBlockState(pos).isAir() &&
                zombie.getWorld().getBlockState(pos.up()).isAir();
    }

    private void startJump() {
        isJumping = true;
        jumpTicks = JUMP_DURATION;
        hasPlacedBlock = false;

        // Mantener la velocidad actual horizontal y solo añadir el componente vertical
        Vec3d currentVel = zombie.getVelocity();
        zombie.setVelocity(currentVel.x, 0.42D, currentVel.z);

        // Un muy ligero ajuste hacia adelante para compensar
        Vec3d lookVec = zombie.getRotationVector();
        zombie.setVelocity(
                zombie.getVelocity().add(lookVec.x * 0.05, 0, lookVec.z * 0.05)
        );
    }

    private void handleJumpingState() {
        if (isJumping) {
            jumpTicks--;

            // Colocar el bloque en el punto más alto del salto
            if (jumpTicks == JUMP_DURATION / 2 && !hasPlacedBlock && targetBuildPos != null && canBuildAt(targetBuildPos)) {
                placeBlock(targetBuildPos);
                hasPlacedBlock = true;

                // Ajustar ligeramente la posición del zombie sobre el bloque
                zombie.refreshPositionAndAngles(
                        targetBuildPos.getX() + 0.5,
                        targetBuildPos.getY() + 1,
                        targetBuildPos.getZ() + 0.5,
                        zombie.getYaw(),
                        zombie.getPitch()
                );
            }

            if (jumpTicks <= 0) {
                isJumping = false;
                targetBuildPos = null;
                hasPlacedBlock = false;
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        hasBlock = false;
        updateZombieHeldItem();
        zombie.getWorld().setBlockState(pos, Blocks.DIRT.getDefaultState());
        buildCooldown = BUILD_COOLDOWN_TIME;
        hasBlock = true;
        updateZombieHeldItem();
    }

    @Override
    public void tick() {
        if (!ZombieEnhancementMechanic.isZombieEnhancementActive() || target == null) {
            return;
        }

        updateZombieHeldItem();
        handleJumpingState();

        if (buildCooldown > 0) {
            buildCooldown--;
            return;
        }

        if (!isJumping) {
            BlockPos zombiePos = zombie.getBlockPos();
            Vec3d directionToPlayer = target.getPos().subtract(zombie.getPos()).normalize();

            BlockPos potentialBuildPos = zombiePos.add(
                    (int)Math.round(directionToPlayer.x),
                    0,
                    (int)Math.round(directionToPlayer.z)
            );

            if (canBuildAt(potentialBuildPos)) {
                targetBuildPos = potentialBuildPos;
                startJump();

                zombie.getLookControl().lookAt(
                        potentialBuildPos.getX() + 0.5,
                        potentialBuildPos.getY(),
                        potentialBuildPos.getZ() + 0.5
                );
            }
        }

        // Movimiento hacia el jugador solo cuando no está saltando
        if (!isJumping && target != null) {
            zombie.getNavigation().startMovingTo(
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    1.2D
            );
        }
    }

    @Override
    public boolean shouldContinue() {
        return canStart() || isJumping;
    }

    @Override
    public void stop() {
        target = null;
        isJumping = false;
        targetBuildPos = null;
        hasPlacedBlock = false;
        if (!ZombieEnhancementMechanic.isZombieEnhancementActive()) {
            zombie.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
    }
}