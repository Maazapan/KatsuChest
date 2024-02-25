package com.github.maazapan.katsuchest.utils.file;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class FileManager {

    private final Plugin plugin;
    private FileType fileType;

    private final String prefix;

    public FileManager(KatsuChest plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getPrefix();
    }

    public FileManager(KatsuChest plugin, FileType fileType) {
        this.plugin = plugin;
        this.fileType = fileType;
        this.prefix = plugin.getPrefix();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder() + "/" + fileType.getFileName());
        YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            plugin.saveResource(fileType.getFileName(), false);
        }
    }

    public int getInt(String path, FileType fileType) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.getInt(path);
    }

    public int getInt(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.getInt(path);
    }

    public List<String> getStringList(String path, FileType fileType) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.getStringList(path);
    }

    public List<String> getStringList(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.getStringList(path);
    }

    public String get(String path, FileType fileType) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));

        if (!config.isSet(path)) {
            return null;
        }
        return KatsuUtils.coloredHex(config.getString(path));
    }

    public String get(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));

        if (!config.isSet(path)) {
            return null;
        }
        return KatsuUtils.coloredHex(config.getString(path));
    }

    public String getWithPrefix(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));


        if (!config.isSet(path)) {
            return null;
        }
        return KatsuUtils.coloredHex(prefix + config.getString(path));
    }

    public String getWithPrefix(String path, FileType fileType) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));

        if (!config.isSet(path)) {
            return null;
        }
        return KatsuUtils.coloredHex(prefix + config.getString(path));
    }

    public boolean contains(String path, FileType fileType) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.contains(path);
    }

    public boolean contains(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.contains(path);
    }

    public void set(String path, Object object, FileType fileType) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        config.set(path, object);
    }

    public void set(String path, Object object) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        config.set(path, object);
    }

    public FileConfiguration toConfig() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
    }

    public FileConfiguration toConfig(FileType fileType) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
    }

    public boolean getBoolean(String path) {

        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.getBoolean(path);
    }

    public double getDouble(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.getDouble(path);
    }


    public ConfigurationSection getSection(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        return config.getConfigurationSection(path);
    }

    public void save() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));

        try {
            config.save(new File(plugin.getDataFolder() + "/" + fileType.getFileName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
