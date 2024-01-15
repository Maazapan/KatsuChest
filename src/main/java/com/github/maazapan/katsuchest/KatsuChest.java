package com.github.maazapan.katsuchest;

import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.manager.LoaderManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class KatsuChest extends JavaPlugin {

    private ChestManager chestManager;
    private LoaderManager loaderManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.chestManager = new ChestManager(this);
        this.loaderManager = new LoaderManager(this);
        loaderManager.load();
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
}
