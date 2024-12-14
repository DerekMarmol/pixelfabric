package com.pixelfabric.updater;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ModAutoUpdater {
    private static final String VERSION_URL = "https://raw.githubusercontent.com/DerekX01/pixelfabric/refs/heads/main/version.txt";
    private static final String MOD_ID = "pixelfabric";
    private final Path modsFolder;
    private final String currentVersion;

    public ModAutoUpdater(String currentVersion) {
        this.currentVersion = currentVersion;
        this.modsFolder = FabricLoader.getInstance().getGameDir().resolve("mods");
        registerCommands();
        registerShutdownHook();
    }


    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("pixelfabric")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("notify")
                                .then(CommandManager.argument("version", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            String version = StringArgumentType.getString(context, "version");
                                            notifyPlayers(context.getSource(), version);
                                            return 1;
                                        })))));
    }

    private void notifyPlayers(ServerCommandSource source, String version) {
        source.getServer().getPlayerManager().broadcast(
                Text.literal("PIXELFABRIC_UPDATE_AVAILABLE" + version),
                false
        );
        source.sendMessage(Text.literal("§aNotificación enviada a los jugadores."));
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                JsonObject versionInfo = getLatestVersionInfo();
                String latestVersion = versionInfo.get("version").getAsString();

                if (isNewerVersion(latestVersion, currentVersion)) {
                    String downloadUrl = versionInfo.get("download_url").getAsString();
                    System.out.println("Nueva versión encontrada durante el cierre: " + latestVersion);
                    updateMod(downloadUrl, latestVersion);
                }
            } catch (Exception e) {
                System.err.println("Error durante la verificación de actualización al cierre: " + e.getMessage());
            }
        }));
    }

    public void checkAndUpdate() {
        try {
            JsonObject versionInfo = getLatestVersionInfo();
            String latestVersion = versionInfo.get("version").getAsString();

            if (isNewerVersion(latestVersion, currentVersion)) {
                String downloadUrl = versionInfo.get("download_url").getAsString();
                System.out.println("Nueva versión encontrada: " + latestVersion);
                updateMod(downloadUrl, latestVersion);
            }
        } catch (Exception e) {
            System.err.println("Error durante la actualización: " + e.getMessage());
        }
    }

    private JsonObject getLatestVersionInfo() throws IOException {
        URL url = new URL(VERSION_URL);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return new Gson().fromJson(reader, JsonObject.class);
        }
    }

    private boolean isNewerVersion(String latestVersion, String currentVersion) {
        String[] latest = latestVersion.split("\\.");
        String[] current = currentVersion.split("\\.");

        for (int i = 0; i < Math.min(latest.length, current.length); i++) {
            int l = Integer.parseInt(latest[i]);
            int c = Integer.parseInt(current[i]);
            if (l > c) return true;
            if (l < c) return false;
        }
        return latest.length > current.length;
    }

    private void updateMod(String downloadUrl, String newVersion) throws IOException {
        Path tempFile = Files.createTempFile(MOD_ID + "-" + newVersion, ".jar");

        System.out.println("Descargando nueva versión...");
        FileUtils.copyURLToFile(new URL(downloadUrl), tempFile.toFile());

        File[] oldVersions = modsFolder.toFile().listFiles((dir, name) ->
                name.startsWith(MOD_ID) && name.endsWith(".jar"));

        if (oldVersions != null) {
            for (File oldVersion : oldVersions) {
                System.out.println("Eliminando versión anterior: " + oldVersion.getName());
                oldVersion.delete();
            }
        }

        Path targetPath = modsFolder.resolve(MOD_ID + "-" + newVersion + ".jar");
        Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Actualización completada. La nueva versión se cargará en el próximo inicio.");
    }
}