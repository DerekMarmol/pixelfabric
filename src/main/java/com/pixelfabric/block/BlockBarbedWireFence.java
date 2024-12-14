package com.pixelfabric.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockBarbedWireFence extends PaneBlock {
    private final VoxelShape[] cullingShapes;

    public BlockBarbedWireFence(FabricBlockSettings settings) {
        super(settings
                .mapColor(MapColor.IRON_GRAY)
                .nonOpaque()
                .requiresTool()
                .strength(2.0f)
                .sounds(BlockSoundGroup.METAL)
                .noCollision()
                // Estas propiedades son cruciales para el renderizado correcto
                .allowsSpawning((state, world, pos, type) -> false)
                .solidBlock((state, world, pos) -> false)
                .suffocates((state, world, pos) -> false)
                .blockVision((state, world, pos) -> false));

        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(WATERLOGGED, false));

        this.cullingShapes = this.createShapes(2.0F, 0.0F, 16.0F, 16.0F, 16.0F);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return this.cullingShapes[this.getShapeIndex(state)];
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;  // Cambiado de CUTOUT a MODEL
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.slowMovement(state, new Vec3d(0.25, 0.05000000074505806, 0.25));
        if (!world.isClient) {
            entity.damage(world.getDamageSources().generic(), 1.0f);

            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.SLOWNESS,
                                20,
                                1,
                                false,
                                false,
                                false
                        )
                );
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, WATERLOGGED);
    }
}
