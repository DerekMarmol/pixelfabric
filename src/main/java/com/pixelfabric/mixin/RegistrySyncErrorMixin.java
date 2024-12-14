package com.pixelfabric.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Mixin(ClientPlayNetworkHandler.class)
public class RegistrySyncErrorMixin {

    @Inject(method = "onDisconnect", at = @At("HEAD"), cancellable = true)
    private void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        Text originalReason = packet.getReason();

        if (originalReason.getString().contains("registry entries that are unknown to this client")) {
            // Mostrar pantalla personalizada
            MinecraftClient.getInstance().setScreen(new Screen(Text.literal("Mod Desactualizado")) {
                @Override
                protected void init() {
                    // Botón para cerrar
                    this.addDrawableChild(ButtonWidget.builder(Text.literal("Entendido"), button -> {
                                MinecraftClient.getInstance().setScreen(null);
                            })
                            .dimensions(this.width / 2 - 100, this.height / 2 + 50, 200, 20)
                            .build());
                }

                @Override
                public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
                    // Renderizar fondo semi-transparente
                    context.fillGradient(0, 0, this.width, this.height, 0x80000000, 0x80000000);

                    // Texto principal
                    context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§c¡Mod PixelFabric Desactualizado!").asOrderedText(),
                            this.width / 2, this.height / 2 - 50, 0xFFFFFF);

                    // Instrucciones
                    context.drawCenteredTextWithShadow(this.textRenderer,
                            Text.literal("§6Por favor sigue estos pasos:").asOrderedText(),
                            this.width / 2, this.height / 2 - 30, 0xFFFF00);

                    context.drawCenteredTextWithShadow(this.textRenderer,
                            Text.literal("1. Cierra completamente Minecraft").asOrderedText(),
                            this.width / 2, this.height / 2 - 20, 0xFFFFFF);

                    context.drawCenteredTextWithShadow(this.textRenderer,
                            Text.literal("2. Espera 10 segundos").asOrderedText(),
                            this.width / 2, this.height / 2 - 10, 0xFFFFFF);

                    context.drawCenteredTextWithShadow(this.textRenderer,
                            Text.literal("3. Vuelve a abrir el juego").asOrderedText(),
                            this.width / 2, this.height / 2, 0xFFFFFF);

                    super.render(context, mouseX, mouseY, delta);
                }

                @Override
                public boolean shouldCloseOnEsc() {
                    return false;
                }
            });

            // Cancelar la desconexión predeterminada
            ci.cancel();
        }
    }
}
