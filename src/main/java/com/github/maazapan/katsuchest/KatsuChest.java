package com.github.maazapan.katsuchest;

import com.github.maazapan.katsuchest.api.integrations.IntegrationManager;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.manager.LoaderManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class KatsuChest extends JavaPlugin {

    private ChestManager chestManager;
    private IntegrationManager integrationManager;
    private LoaderManager loaderManager;
    private static KatsuChest instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.chestManager = new ChestManager(this);
        this.loaderManager = new LoaderManager(this);
        this.integrationManager = new IntegrationManager();

        instance = this;
        loaderManager.load();
        integrationManager.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        loaderManager.disable();
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    public String getPrefix() {
        return getConfig().getString("config.prefix");
    }

    public static KatsuChest getInstance() {
        return instance;
    }

    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    public LoaderManager getLoaderManager() {
        return loaderManager;
    }
}
