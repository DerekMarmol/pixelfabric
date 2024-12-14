package com.pixelfabric.client;

import com.pixelfabric.animation.RuletaAnimationSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class RuletaHud {
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            // Usar el handler del sistema principal
            RuletaAnimationSystem.ClientAnimationHandler handler = RuletaAnimationSystem.getClientHandler();
            if (handler != null) {
                handler.render(drawContext);
            }
        });
    }
}