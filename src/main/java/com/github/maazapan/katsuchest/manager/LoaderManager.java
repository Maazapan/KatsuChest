package com.github.maazapan.katsuchest.manager;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.commands.ChestCommand;
import com.github.maazapan.katsuchest.listener.ChestListener;
import com.github.maazapan.katsuchest.listener.InventoryListener;
import com.github.maazapan.katsuchest.listener.PlayerListener;
import com.github.maazapan.katsuchest.manager.loader.ChestLoader;
import com.github.maazapan.katsuchest.utils.file.FileCreator;

public class LoaderManager {

    private final KatsuChest plugin;
    private ChestLoader chestLoader;

    public LoaderManager(KatsuChest plugin) {
        this.plugin = plugin;
    }


    public void load() {
        loadListener();
        loadCommands();
        loadFiles();

        this.chestLoader = new ChestLoader(plugin);
        this.chestLoader.load();
    }

    public void disable() {
        this.chestLoader.save();
    }

    /**
     * Loads all the listeners.
     */
    private void loadListener() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ChestListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    private void loadCommands() {
        plugin.getCommand("katsuchest").setExecutor(new ChestCommand(plugin));
        plugin.getCommand("katsuchest").setTabCompleter(new ChestCommand(plugin));
    }

    /**
     * Loads all the files.
     */
    private void loadFiles() {
        new FileCreator("config.yml", plugin.getDataFolder().getPath(), plugin).create();
        new FileCreator("messages.yml", plugin.getDataFolder().getPath(), plugin).create();
    }
}
