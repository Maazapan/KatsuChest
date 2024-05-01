package com.github.maazapan.katsuchest.manager;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.api.integrations.bstats.Metrics;
import com.github.maazapan.katsuchest.api.integrations.update.Update;
import com.github.maazapan.katsuchest.chest.manager.RecipeManager;
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
        loadRecipes();
        loadHooks();

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

    /**
     * Loads all the recipes.
     */
    private void loadRecipes() {
        RecipeManager recipeManager = new RecipeManager(plugin);
        recipeManager.registerRecipes();
    }

    private void loadHooks() {
        Metrics metrics = new Metrics(plugin, 21066);

        new Update(plugin, 115171).getVersion(version -> {
            if (plugin.getDescription().getVersion().equals(version)) {
                plugin.getLogger().info("There is not a new update available.");

            } else {
                plugin.getLogger().info("There is a new update available https://www.spigotmc.org/resources/115171");
            }
        });
    }
}
