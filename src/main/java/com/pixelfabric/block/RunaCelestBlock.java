package com.pixelfabric.block;

import com.pixelfabric.item.ModItems;
import com.pixelfabric.mission.AbstractKillMission;
import com.pixelfabric.mission.Mission;
import com.pixelfabric.mission.MissionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.World;

public class RunaCelestBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public RunaCelestBlock() {
        super(FabricBlockSettings.create()
                .strength(3.0f, 15.0f)
                .sounds(BlockSoundGroup.STONE)
                .nonOpaque()
                .luminance(state -> state.get(ACTIVE) ? 15 : 0));
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ACTIVE, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            ItemStack heldItem = player.getMainHandStack();

            if (player instanceof ServerPlayerEntity serverPlayer) {
                Mission activeMission = MissionManager.getInstance().getActiveMission();

                if (activeMission == null) {
                    player.sendMessage(Text.literal("No hay misión activa actualmente.")
                            .formatted(Formatting.RED), false);
                    playFailureEffects(world, pos);
                    return ActionResult.SUCCESS;
                }

                if (MissionManager.getInstance().hasCompletedMission(player.getUuid())) {
                    player.sendMessage(Text.literal("Ya has completado la misión diaria.")
                            .formatted(Formatting.YELLOW), false);
                    playFailureEffects(world, pos);
                    return ActionResult.SUCCESS;
                }

                if (heldItem.isOf(ModItems.BLUECOIN)) {
                    boolean missionCompleted = MissionManager.getInstance().tryCompleteMission(serverPlayer);

                    if (missionCompleted) {
                        playSuccessEffects(world, pos, state);
                        heldItem.decrement(1);
                        return ActionResult.SUCCESS;
                    }
                } else {
                    if (activeMission instanceof AbstractKillMission killMission) {
                        player.sendMessage(Text.literal("Misión actual: " + activeMission.getDescription())
                                .formatted(Formatting.GOLD), false);
                        player.sendMessage(Text.literal("Progreso: " +
                                        killMission.getCurrentKills(player.getUuid()) + "/" +
                                        killMission.getRequiredKills())
                                .formatted(Formatting.AQUA), false);
                    } else {
                        player.sendMessage(Text.literal("Misión actual: " + activeMission.getDescription())
                                .formatted(Formatting.GOLD), false);
                    }

                    player.sendMessage(Text.literal("Necesitas una Deathcoin azul para reclamar la recompensa.")
                            .formatted(Formatting.YELLOW), false);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    private void playSuccessEffects(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(ACTIVE, true));
        world.scheduleBlockTick(pos, this, 20); // Desactivar después de 1 segundo // Desactivar después de 1 segundo

        if (world instanceof ServerWorld serverWorld) {
            double centerX = pos.getX() + 0.5;
            double centerZ = pos.getZ() + 0.5;
            for (int i = 0; i < 20; i++) {
                double angle = i * Math.PI * 2 / 20;
                double radius = 0.5;
                double x = centerX + Math.cos(angle) * radius;
                double z = centerZ + Math.sin(angle) * radius;
                double y = pos.getY() + (i / 20.0) * 2;

                serverWorld.spawnParticles(ParticleTypes.END_ROD,
                        x, y, z,
                        1, 0, 0, 0, 0.05);
            }

            serverWorld.spawnParticles(ParticleTypes.GLOW,
                    centerX, pos.getY() + 2, centerZ,
                    15, 0.5, 0.5, 0.5, 0.1);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP,
                SoundCategory.BLOCKS, 1.0F, 1.2F);
    }

    private void playFailureEffects(World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld) {
            double centerX = pos.getX() + 0.5;
            double centerZ = pos.getZ() + 0.5;
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI * 2 / 8;
                double radius = 0.3;
                double x = centerX + Math.cos(angle) * radius;
                double z = centerZ + Math.sin(angle) * radius;

                serverWorld.spawnParticles(ParticleTypes.SMOKE,
                        x, pos.getY() + 1.5, z,
                        3, 0.1, 0.1, 0.1, 0.02);
            }
        }

        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.BLOCKS, 0.7F, 0.9F);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(ACTIVE)) {
            world.setBlockState(pos, state.with(ACTIVE, false));
        }
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape base = VoxelShapes.union(
                createCuboidShape(2, 2, 2, 14, 21, 14),
                createCuboidShape(1, 21, 1, 15, 23, 15),
                createCuboidShape(1, 0, 1, 15, 2, 15)
        );
        return base;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}