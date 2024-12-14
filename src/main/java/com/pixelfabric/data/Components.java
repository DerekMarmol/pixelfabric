package com.pixelfabric.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class Components implements EntityComponentInitializer {
    public static final ComponentKey<IPlayerHealthData> PLAYER_HEALTH =
            ComponentRegistry.getOrCreate(new Identifier("pixelfabric", "player_health"), IPlayerHealthData.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, PLAYER_HEALTH)
                .impl(PlayerHealthComponent.class)
                .respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY)
                .end(player -> new PlayerHealthComponent());
    }
}
