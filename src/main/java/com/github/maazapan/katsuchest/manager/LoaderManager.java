package com.github.maazapan.katsuchest.manager;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.commands.ChestCommand;
import com.github.maazapan.katsuchest.listener.PlayerListener;
import com.github.maazapan.katsuchest.utils.file.FileCreator;

public class LoaderManager {

    private final KatsuChest plugin;

    public LoaderManager(KatsuChest plugin) {
        this.plugin = plugin;
    }


    public void load() {
        loadListener();
        loadCommands();
        loadFiles();
    }

    public void disable() {

    }

    /**
     * Loads all the listeners.
     */
    private void loadListener() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
    }

    private void loadCommands() {
        plugin.getCommand("katsuchest").setExecutor(new ChestCommand(plugin));
        plugin.getCommand("katsuchest").setTabCompleter(new ChestCommand(plugin));
    }

    /**
     * Loads all the files.
     */
    private void loadFiles() {
        new FileCreator("config.yml", plugin.getDataFolder(), plugin);
        new FileCreator("messages.yml", plugin.getDataFolder(), plugin);
    }
}
