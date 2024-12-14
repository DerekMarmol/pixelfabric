package com.pixelfabric.animation;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import java.util.*;
import java.util.concurrent.*;

public class RuletaAnimationSystem {
    private static final float ANIMATION_DURATION = 15000f;
    public static final Identifier PACKET_ID = new Identifier("pixelfabric", "ruleta_animation");
    private static final SoundEvent RULETA_SOUND = SoundEvent.of(new Identifier("pixelfabric", "ruleta"));
    private static final SoundEvent REVIIL_SOUND = SoundEvent.of(new Identifier("pixelfabric", "reviil"));
    private static final SoundEvent MUERTE_SOUND = SoundEvent.of(new Identifier("pixelfabric", "muerte"));

    private static final Map<String, AnimationInfo> ANIMATIONS = new HashMap<>();
    private static final TextureManager textureManager = new TextureManager();
    private static final AnimationScheduler scheduler = new AnimationScheduler();
    private static final NetworkOptimizer networkOptimizer = new NetworkOptimizer();
    private static ClientAnimationHandler clientHandler;

    static {
        ANIMATIONS.put("roja", new AnimationInfo("roja", 295, true, ANIMATION_DURATION));
        ANIMATIONS.put("verde", new AnimationInfo("verde", 290, false, ANIMATION_DURATION));
        ANIMATIONS.put("amarilla", new AnimationInfo("amarilla", 288, false, ANIMATION_DURATION));
        ANIMATIONS.put("naranja", new AnimationInfo("naranja", 287, false, ANIMATION_DURATION));
        ANIMATIONS.put("turqueza", new AnimationInfo("turqueza", 287, false, ANIMATION_DURATION));
        ANIMATIONS.put("muerte", new AnimationInfo("muerte", 92, false, 5000f));
        ANIMATIONS.put("momentoreviil", new AnimationInfo("momentoreviil", 199, false, 10000f));
    }

    private static class AnimationInfo {
        String name;
        int frameCount;
        boolean playReviil;
        float duration;
        float frameTime;

        AnimationInfo(String name, int frameCount, boolean playReviil, float duration) {
            this.name = name;
            this.frameCount = frameCount;
            this.playReviil = playReviil;
            this.duration = duration;
            this.frameTime = duration / frameCount;
        }
    }

    // Gestor optimizado de texturas
    private static class TextureManager {
        private final Map<String, Identifier[]> textureCache = new ConcurrentHashMap<>();

        public Identifier[] getTextures(String animationName) {
            return textureCache.computeIfAbsent(animationName, this::loadTexturesForAnimation);
        }

        private Identifier[] loadTexturesForAnimation(String animationName) {
            AnimationInfo info = ANIMATIONS.get(animationName);
            if (info == null) return new Identifier[0];

            Identifier[] textures = new Identifier[info.frameCount];
            for (int i = 0; i < info.frameCount; i++) {
                String texturePath = String.format("textures/gui/ruleta/%s/%s-%d.png",
                        animationName, animationName, i + 1);
                textures[i] = new Identifier("pixelfabric", texturePath);
            }
            return textures;
        }

        public void clearCache() {
            textureCache.clear();
        }
    }

    // Gestor optimizado de threads y programación
    private static class AnimationScheduler {
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "Animation-Scheduler");
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        public void scheduleAnimation(Runnable task, long delay) {
            scheduler.schedule(() -> {
                MinecraftClient.getInstance().execute(task);
            }, delay, TimeUnit.MILLISECONDS);
        }

        public void shutdown() {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // Optimizador de red
    private static class NetworkOptimizer {
        private static final int BATCH_SIZE = 10;
        private final Queue<AnimationPacket> packetQueue = new ConcurrentLinkedQueue<>();

        private record AnimationPacket(String type, ServerPlayerEntity player) {}

        public void queueAnimation(String type, ServerPlayerEntity player) {
            packetQueue.offer(new AnimationPacket(type, player));
            if (packetQueue.size() >= BATCH_SIZE) {
                flushQueue();
            }
        }

        private void flushQueue() {
            List<AnimationPacket> batch = new ArrayList<>();
            AnimationPacket packet;
            while ((packet = packetQueue.poll()) != null && batch.size() < BATCH_SIZE) {
                batch.add(packet);
            }

            if (!batch.isEmpty()) {
                sendBatch(batch);
            }
        }

        private void sendBatch(List<AnimationPacket> batch) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(batch.size());
            for (AnimationPacket packet : batch) {
                buf.writeString(packet.type);
                ServerPlayNetworking.send(packet.player, PACKET_ID, buf);
            }
        }
    }

    public static class OptimizedRenderer {
        private record ScreenPosition(int x, int y) {}
        private record RenderData(int width, int height) {}

        private final int imageWidth = 224;
        private final int imageHeight = 224;

        public void render(DrawContext context, String animation, int frame) {
            ScreenPosition pos = calculateScreenPosition();
            Identifier[] textures = textureManager.getTextures(animation);

            if (textures != null && frame < textures.length) {
                context.drawTexture(
                        textures[frame],
                        pos.x,
                        pos.y,
                        0,
                        0,
                        imageWidth,
                        imageHeight,
                        imageWidth,
                        imageHeight
                );
            }
        }

        private ScreenPosition calculateScreenPosition() {
            MinecraftClient client = MinecraftClient.getInstance();
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            return new ScreenPosition(
                    (screenWidth - imageWidth) / 2,
                    (screenHeight - imageHeight) / 2
            );
        }
    }

    // Cliente optimizado
    public static class ClientAnimationHandler {
        private String currentAnimation = null;
        private long startTime = 0;
        private boolean isPlaying = false;
        private AnimationInfo currentInfo = null;
        private String pendingReviilAnimation = null;
        private long reviilStartTime = 0;
        private final OptimizedRenderer renderer = new OptimizedRenderer();

        // Añadir variables para el efecto rojizo
        private static final int RED_OVERLAY_COLOR = 0x55FF0000; // ARGB: semitransparente rojo
        private boolean showingRedOverlay = false;
        private float overlayIntensity = 0.0f;
        private static final float OVERLAY_FADE_DURATION = 1000f;

        public void startAnimation(String animationType) {
            if (!ANIMATIONS.containsKey(animationType)) return;

            // Limpiar la animación anterior antes de comenzar la nueva
            if (isPlaying) {
                isPlaying = false;
            }

            currentAnimation = animationType;
            currentInfo = ANIMATIONS.get(animationType);
            startTime = System.currentTimeMillis();
            isPlaying = true;

            // Reproducir sonido
            SoundEvent sound = animationType.equals("muerte") ? MUERTE_SOUND : RULETA_SOUND;
            playSound(sound);

            if (currentInfo.playReviil) {
                scheduleReviilAnimation();
            }
        }

        private void playSound(SoundEvent sound) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null && client.player != null) {
                client.world.playSound(
                        client.player,
                        client.player.getBlockPos(),
                        sound,
                        SoundCategory.HOSTILE,
                        1.0F,
                        1.0F
                );
            }
        }

        private void scheduleReviilAnimation() {
            scheduler.scheduleAnimation(() -> {
                playSound(REVIIL_SOUND);
                pendingReviilAnimation = "momentoreviil";
                reviilStartTime = System.currentTimeMillis();
            }, 15000);
        }

        public void render(DrawContext context) {
            long currentTime = System.currentTimeMillis();

            // Renderizar animación principal
            if (isPlaying && currentAnimation != null && currentInfo != null) {
                float elapsedTime = currentTime - startTime;
                int frame = (int)((elapsedTime / currentInfo.duration) * currentInfo.frameCount);

                if (frame >= currentInfo.frameCount) {
                    isPlaying = false;
                } else {
                    renderer.render(context, currentAnimation, frame);
                }
            }

            // Renderizar animación de reviil si está pendiente
            if (pendingReviilAnimation != null) {
                AnimationInfo reviilInfo = ANIMATIONS.get(pendingReviilAnimation);
                if (reviilInfo != null) {
                    float elapsedTime = currentTime - reviilStartTime;
                    int frame = (int)((elapsedTime / reviilInfo.duration) * reviilInfo.frameCount);

                    if (frame >= reviilInfo.frameCount) {
                        pendingReviilAnimation = null;
                    } else {
                        renderer.render(context, pendingReviilAnimation, frame);
                    }
                }
            }
        }

        private void renderCurrentAnimation(DrawContext context, long currentTime) {
            float elapsedTime = currentTime - startTime;
            int frame = (int)((elapsedTime / currentInfo.duration) * currentInfo.frameCount);

            if (frame >= currentInfo.frameCount) {
                isPlaying = false;
                return;
            }

            renderer.render(context, currentAnimation, frame);
        }

        private void renderReviilAnimation(DrawContext context, long currentTime) {
            AnimationInfo reviilInfo = ANIMATIONS.get(pendingReviilAnimation);
            if (reviilInfo == null) return;

            float elapsedTime = currentTime - reviilStartTime;
            int frame = (int)((elapsedTime / reviilInfo.duration) * reviilInfo.frameCount);

            if (frame >= reviilInfo.frameCount) {
                pendingReviilAnimation = null;
                return;
            }

            renderer.render(context, pendingReviilAnimation, frame);
        }
    }


    // Inicialización del cliente
    public static void initializeClient() {
        // Inicializar el manejador de animaciones del cliente
        clientHandler = new ClientAnimationHandler();

        // Registrar el listener de paquetes de red
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID, (client, handler, buf, responseSender) -> {
            String animationType = buf.readString();
            client.execute(() -> {
                if (clientHandler != null) {
                    clientHandler.startAnimation(animationType);
                }
            });
        });

        // Registrar el listener de recursos
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier("pixelfabric", "ruleta_textures");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        textureManager.clearCache();
                    }
                }
        );
    }
    public static ClientAnimationHandler getClientHandler() {
        return clientHandler;
    }

    public static void playAnimation(String animationType, ServerPlayerEntity player) {
        if (ANIMATIONS.containsKey(animationType)) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(animationType);
            ServerPlayNetworking.send(player, PACKET_ID, buf);
        }
    }

    public static void cleanup() {
        scheduler.shutdown();
        textureManager.clearCache();
        clientHandler = null;
    }
}