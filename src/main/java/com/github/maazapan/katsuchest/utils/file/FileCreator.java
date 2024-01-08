package com.github.maazapan.katsuchest.utils.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileCreator extends YamlConfiguration {

    private final String name;
    private final File file;
    private final Plugin plugin;

    public FileCreator(String name, File file, Plugin plugin) {
        this.name = name;
        this.plugin = plugin;
        this.file = new File(file, name);
        saveDefault();
        reload();
    }

    public void reload() {
        try {
            super.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            super.save(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        plugin.saveResource(name, false);
    }
}