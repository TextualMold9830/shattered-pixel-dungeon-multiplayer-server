package com.shatteredpixel.shatteredpixeldungeon.server.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginUtils {
    public static Path getConfigDirectory(Plugin plugin){
        if (!Files.isDirectory(Paths.get("config/"+plugin.manifest.getName()))){
            try {
                Files.createDirectories(Paths.get("config/"+plugin.manifest.getName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Paths.get("config/"+plugin.manifest.getName());
    }
    public static File getDefaultConfigFile(Plugin plugin){
        Path configPath = Paths.get(getConfigDirectory(plugin) + "/config.txt");
        if (!Files.exists(configPath)){
            try {
                Files.createFile(configPath);
                Files.write(configPath, plugin.defaultConfig().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Paths.get("config/"+plugin.manifest.getName()+"/config.txt").toFile();
    }
    public static String getConfigData(Plugin plugin){
        try {
            return new String(Files.readAllBytes(getDefaultConfigFile(plugin).toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return plugin.defaultConfig();
    }
}
