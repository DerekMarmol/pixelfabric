package com.pixelfabric;

import com.eliotlash.mclib.math.functions.classic.Mod;
import com.pixelfabric.animation.RuletaAnimationSystem;
import com.pixelfabric.block.ModBlocks;
import com.pixelfabric.client.CreeperFlashEffect;
import com.pixelfabric.client.FlashEffectClient;
import com.pixelfabric.client.KeyInputHandler;
import com.pixelfabric.client.RuletaHud;
import com.pixelfabric.entity.ModEntities;
import com.pixelfabric.updater.ModAutoUpdaterClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.pixelfabric.entity.client.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderLayer;

public class PixelFabricClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        KeyInputHandler.register();
        FlashEffectClient.register();
        CreeperFlashEffect.register();
        ModAutoUpdaterClient.initialize();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BARBED_WIRE_FENCE, RenderLayer.getCutout());

        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            FlashEffectClient.render(matrices.getMatrices(), tickDelta);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FlashEffectClient.clientTick();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            CreeperFlashEffect.clientTick();
        });

        // Registrar el evento de renderizado
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            CreeperFlashEffect.render(matrixStack.getMatrices(), tickDelta);
        });
        RuletaAnimationSystem.initializeClient();
        RuletaHud.register();
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            RuletaAnimationSystem.cleanup();
        });

        EntityRendererRegistry.register(ModEntities.BONE_SPIDER, BoneSpiderRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHAOS_PHANTOM, ChaosPhantomRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOOBLOOM, MoobloomRenderer::new);
        EntityRendererRegistry.register(ModEntities.ATURTED_PHANTOM, AturdedPhantomRenderer::new);
        EntityRendererRegistry.register(ModEntities.EXPLODING_SKELETON, ExplodingSkeletonRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVASQUID, Lava_SquidRenderer::new);
        EntityRendererRegistry.register(ModEntities.CANDIK, CandikRenderer::new);
        EntityRendererRegistry.register(ModEntities.MINER_ZOMBIE, MinerZombieRenderer::new);
        EntityRendererRegistry.register(ModEntities.Skull, SkullRenderer::new);
        EntityRendererRegistry.register(ModEntities.BARNACLE, BarnacleRenderer::new);
        EntityRendererRegistry.register(ModEntities.Altar, AltarRenderer::new);
        EntityRendererRegistry.register(ModEntities.Soldier_Bee, Soldier_BeeRenderer::new);
        EntityRendererRegistry.register(ModEntities.Golem, GolemRenderer::new);
        EntityRendererRegistry.register(ModEntities.Pumpkin, PumpkinFiendeRenderer::new);
        EntityRendererRegistry.register(ModEntities.Owlbear, OwlbearRenderer::new);
        EntityRendererRegistry.register(ModEntities.Hellhound, HellhoundRenderer::new);
        EntityRendererRegistry.register(ModEntities.Wraith, WraithRenderer::new);
        EntityRendererRegistry.register(ModEntities.Wildfire, WildfireRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFERNAL_BULL, InfernalBullRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVA_SPIDER, Octana_ExplodeRenderer::new);
        EntityRendererRegistry.register(ModEntities.CANDLE_SWORD, Candle_SwordRenderer::new);
        EntityRendererRegistry.register(ModEntities.GENERATOR_ENTITY, GeneratorRenderer::new);
        EntityRendererRegistry.register(ModEntities.PINATA_BURRITO, PinataBurritoRenderer::new);
        EntityRendererRegistry.register(ModEntities.ZOMBIE_TANK, ZombieTankRenderer::new);
        EntityRendererRegistry.register(ModEntities.ANGRY_CHICKEN, AngryChickenRenderer::new);
        EntityRendererRegistry.register(ModEntities.TOXIN_SPIDER, ToxinSpiderRenderer::new);
        EntityRendererRegistry.register(ModEntities.TOXIN_PROJECTILE, ToxinProjectileRenderer::new);

    }
}
