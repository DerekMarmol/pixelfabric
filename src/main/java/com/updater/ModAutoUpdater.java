package com.updater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ModAutoUpdater implements ModInitializer {
    private static final String VERSION_CHECK_URL = "https://raw.githubusercontent.com/DerekMarmol/pixelfabric/refs/heads/master/version.txt";
    private static final String CURRENT_VERSION = "1.0.0"; // Tu versión actual

    @Override
    public void onInitialize() {
        checkForUpdates();
    }

    public void checkForUpdates() {
        try {
            // Verificar la última versión disponible
            URL versionUrl = new URL(VERSION_CHECK_URL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(versionUrl.openStream()));

            // Parsear el JSON del archivo version.txt
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            String latestVersion = jsonObject.get("version").getAsString();
            String downloadUrl = jsonObject.get("download_url").getAsString();
            reader.close();

            if (!latestVersion.equals(CURRENT_VERSION)) {
                downloadUpdate(downloadUrl);
            }
        } catch (IOException e) {
            System.out.println("Error al verificar actualizaciones: " + e.getMessage());
        }
    }

    private void downloadUpdate(String downloadUrl) {
        try {
            // Obtener la ruta del mod actual
            Path modsFolder = FabricLoader.getInstance().getGameDir().resolve("mods");
            Path currentModPath = modsFolder.resolve("tu-mod-actual.jar");

            // Descargar la nueva versión
            URL url = new URL(downloadUrl);
            Path tempFile = Files.createTempFile("mod-update", ".jar");
            Files.copy(url.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Programar la actualización para cuando el juego se cierre
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    Files.move(tempFile, currentModPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            System.out.println("¡Actualización descargada! Se instalará cuando reinicies el juego.");
        } catch (IOException e) {
            System.out.println("Error al descargar la actualización: " + e.getMessage());
        }
    }
}
