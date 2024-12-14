package com.pixelfabric.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import java.util.*;

public class MinerZombieEntity extends ZombieEntity {
    private int breakingTime;
    private BlockPos breakingPos = null;
    private int breakingProgress = -1;
    private static final int DETECTION_RANGE = 16; // Rango de detección a través de paredes

    public MinerZombieEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
        this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.add(3, new MinerZombieBreakBlockGoal(this, 23)); // Prioridad más alta que el ataque
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, TurtleEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createMinerZombieAttributes() {
        return ZombieEntity.createZombieAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.285D)  // Aumentado de 0.24 a 0.285
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.5D)
                .add(EntityAttributes.GENERIC_ARMOR, 3.0D);
    }

    private static class MinerZombieBreakBlockGoal extends Goal {
        private final MinerZombieEntity zombie;
        private LivingEntity target;
        private BlockPos blockToBreak = null;
        private boolean isBreaking = false;
        private int breakingTimeout = 0;
        private static final int MAX_BREAK_TIME = 15; // Reducido de 20 a 15 ticks (0.75 segundos)
        private static final double DIRECT_PATH_CHECK_DISTANCE = 2.0; // Nueva constante para verificar la distancia de una ruta directa
        private static final double VERTICAL_CHECK_DISTANCE = 4.0; // Nueva constante para verificación vertical
        private static final int MAX_VERTICAL_SEARCH = 3; // Máxima diferencia de altura a buscar
        private static final int MAX_SEARCH_RANGE = 8;
        private int priority;

        public MinerZombieBreakBlockGoal(MinerZombieEntity zombie, int priority) {
            this.zombie = zombie;
            this.priority = priority;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            this.target = findNearestTarget(); // Ahora usa findNearestTarget() en lugar de getTarget()
            if (this.target == null || !this.target.isAlive()) {
                return false;
            }

            if (this.isBreaking && this.blockToBreak != null) {
                return true;
            }

            boolean canReachTarget = this.zombie.getNavigation().isFollowingPath() ||
                    this.zombie.getNavigation().startMovingTo(this.target, 1.0);

            return !canReachTarget && this.zombie.squaredDistanceTo(this.target) > DIRECT_PATH_CHECK_DISTANCE;
        }

        @Override
        public boolean shouldContinue() {
            if (this.isBreaking && this.blockToBreak != null) {
                BlockState state = this.zombie.getWorld().getBlockState(this.blockToBreak);
                return !state.isAir() && this.breakingTimeout <= MAX_BREAK_TIME * 2;
            }
            return false;
        }

        @Override
        public void start() {
            if (!this.isBreaking) {
                BlockPos newBlockToBreak = findBlockToBreak();
                if (newBlockToBreak != null) {
                    this.blockToBreak = newBlockToBreak;
                    this.zombie.breakingPos = this.blockToBreak;
                    this.isBreaking = true;
                    this.breakingTimeout = 0;
                    this.zombie.breakingTime = 0;
                    this.zombie.breakingProgress = 0;
                    this.zombie.getNavigation().stop();
                }
            }
        }

        @Override
        public void stop() {
            if (!this.isBreaking) {
                stopBreaking();
            }
        }

        @Override
        public void tick() {
            if (!this.isBreaking || this.blockToBreak == null) {
                return;
            }

            BlockState state = this.zombie.getWorld().getBlockState(this.blockToBreak);
            if (state.isAir() || this.breakingTimeout > MAX_BREAK_TIME * 2) {
                stopBreaking();
                return;
            }

            this.zombie.getLookControl().lookAt(
                    this.blockToBreak.getX() + 0.5,
                    this.blockToBreak.getY() + 0.5,
                    this.blockToBreak.getZ() + 0.5
            );

            this.zombie.getNavigation().stop();
            this.zombie.breakingTime++;
            this.breakingTimeout++;

            if (this.zombie.breakingTime % 2 == 0) {
                this.zombie.getWorld().playSound(
                        this.blockToBreak.getX(),
                        this.blockToBreak.getY(),
                        this.blockToBreak.getZ(),
                        state.getSoundGroup().getHitSound(),
                        SoundCategory.HOSTILE,
                        1.0F,
                        0.75F,
                        false
                );

                this.zombie.breakingProgress = (int) ((float) this.zombie.breakingTime / MAX_BREAK_TIME * 10.0F);
                this.zombie.getWorld().setBlockBreakingInfo(
                        this.zombie.getId(),
                        this.blockToBreak,
                        Math.min(this.zombie.breakingProgress, 9)
                );

                if (this.zombie.breakingTime >= MAX_BREAK_TIME) {
                    this.zombie.getWorld().breakBlock(this.blockToBreak, true, this.zombie);
                    stopBreaking();
                }
            }
        }

        private void stopBreaking() {
            if (this.zombie.breakingPos != null) {
                this.zombie.getWorld().setBlockBreakingInfo(this.zombie.getId(), this.zombie.breakingPos, -1);
            }
            this.zombie.breakingTime = 0;
            this.zombie.breakingPos = null;
            this.zombie.breakingProgress = -1;
            this.isBreaking = false;
            this.blockToBreak = null;
            this.breakingTimeout = 0;
        }

        private BlockPos findBlockToBreak() {
            if (this.target == null) return null;

            Vec3d zombiePos = this.zombie.getPos();
            Vec3d targetPos = this.target.getPos();

            // Calcular la dirección directa entre zombie y objetivo
            Vec3d direction = targetPos.subtract(zombiePos).normalize();

            // Buscar bloques en la línea de visión directa
            BlockPos blockToBreak = findBlockInDirectPath(zombiePos, targetPos, direction);

            return blockToBreak;
        }

        private BlockPos findBlockInDirectPath(Vec3d start, Vec3d end, Vec3d direction) {
            // Obtener la distancia máxima a revisar
            double maxDistance = start.distanceTo(end);

            for (double distance = 1.0; distance <= maxDistance; distance += 1.0) {
                Vec3d checkPos = start.add(direction.multiply(distance));
                BlockPos blockPos = new BlockPos((int)checkPos.x, (int)checkPos.y, (int)checkPos.z);

                // Verificar si el bloque bloquea el camino directo
                if (isBlockObstructingPath(blockPos)) {
                    return blockPos;
                }
            }

            return null;
        }

        private boolean isBlockObstructingPath(BlockPos pos) {
            BlockState state = this.zombie.getWorld().getBlockState(pos);

            // Lista de bloques que se consideran obstrucciones
            Block[] obstructionBlocks = {
                    Blocks.STONE,
                    Blocks.DIRT,
                    Blocks.COBBLESTONE,
                    Blocks.DEEPSLATE,
                    Blocks.GRANITE,
                    Blocks.ANDESITE
            };

            // Verificar si el bloque está en la lista de obstrucciones
            boolean isObstructionBlock = Arrays.stream(obstructionBlocks)
                    .anyMatch(block -> state.getBlock() == block);

            return isObstructionBlock &&
                    !state.isAir() &&
                    !state.isIn(BlockTags.IMPERMEABLE);
        }

        private boolean isValidBreakCandidate(BlockPos pos, Vec3d targetPos) {
            BlockState state = this.zombie.getWorld().getBlockState(pos);
            if (state.isAir() || state.isIn(BlockTags.IMPERMEABLE)) {
                return false;
            }

            Block block = state.getBlock();
            boolean isDestructible = block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.COBBLESTONE;

            Vec3d blockCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            return isDestructible && blockCenter.isInRange(targetPos, VERTICAL_CHECK_DISTANCE);
        }

        private double calculateBlockPriority(BlockPos pos, Vec3d zombiePos, Vec3d targetPos) {
            Vec3d blockCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            double distanceToZombie = blockCenter.distanceTo(zombiePos);
            double distanceToTarget = blockCenter.distanceTo(targetPos);

            return distanceToZombie + distanceToTarget * 2.0;
        }

        private LivingEntity findNearestTarget() {
            List<LivingEntity> targets = this.zombie.getWorld().getEntitiesByClass(
                    LivingEntity.class,
                    this.zombie.getBoundingBox().expand(DETECTION_RANGE),
                    (entity) -> entity instanceof PlayerEntity ||
                            entity instanceof MerchantEntity ||
                            entity instanceof IronGolemEntity
            );

            return targets.isEmpty() ? null : targets.get(0);
        }
    }

    private static class BlockPriority {
        private final BlockPos pos;
        private final double priority;

        public BlockPriority(BlockPos pos, double priority) {
            this.pos = pos;
            this.priority = priority;
        }

        public BlockPos getPos() {
            return pos;
        }

        public double getPriority() {
            return priority;
        }
    }
}
