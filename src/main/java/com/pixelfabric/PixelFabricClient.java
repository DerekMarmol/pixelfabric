package com.pixelfabric;

import com.pixelfabric.client.KeyInputHandler;
import com.pixelfabric.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.pixelfabric.entity.client.*;


public class PixelFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyInputHandler.register();

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
        EntityRendererRegistry.register(ModEntities.LAVA_SPIDER, Octana_ExplodeRenderer::new);
        EntityRendererRegistry.register(ModEntities.CANDLE_SWORD, Candle_SwordRenderer::new);
    }
}
