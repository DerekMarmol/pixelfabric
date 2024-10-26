package com.pixelfabric.client;

import com.pixelfabric.entity.custom.SkullEntity;
import com.pixelfabric.network.ModMessages;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY = "PixelFabric";
    public static final String KEY_ESCAPE_SKULL = "Quitar calavera explosiva";

    public static KeyBinding escapeBinding;
    private static boolean wasPressed = false;

    public static void register() {
        escapeBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ESCAPE_SKULL,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_SPACE,
                KEY_CATEGORY
        ));

        registerKeyInputs();
    }

    private static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (escapeBinding.isPressed()) {
                // Continuar mientras la tecla esté presionada
                if (!wasPressed) {
                    // Buscar la calavera más cercana que esté adherida al jugador
                    if (client.player != null) {
                        for (Entity entity : client.world.getEntities()) {
                            if (entity instanceof SkullEntity skull && skull.isAttached()) {
                                // Enviar el paquete al servidor
                                ClientPlayNetworking.send(
                                        ModMessages.SPACE_PRESS_ID,
                                        ModMessages.createSpacePressPacket(skull.getId())
                                );
                                break;
                            }
                        }
                    }
                }
            }
            wasPressed = escapeBinding.isPressed();
        });
    }
}