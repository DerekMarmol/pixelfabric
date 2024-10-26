package com.pixelfabric.mixin;

import com.pixelfabric.missions.MissionManager;
import net.minecraft.block.BarrierBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BarrierBlock.class)
public abstract class BarrierBlockMixin extends Block {

    public BarrierBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            System.out.println("Interacción detectada con la barrera!"); // Debug

            if (player instanceof ServerPlayerEntity serverPlayer) {
                boolean result = MissionManager.getInstance().tryCompleteMission(serverPlayer);
                System.out.println("Resultado de la misión: " + result); // Debug
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}