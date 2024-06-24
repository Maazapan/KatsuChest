package com.github.maazapan.katsuchest.utils.gui;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import com.github.maazapan.katsuchest.utils.itemstack.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryGUI implements InventoryHolder {

    private final KatsuChest plugin;
    private final String path;

    private Inventory inventory;

    public InventoryGUI(KatsuChest plugin, String path) {
        this.plugin = plugin;
        this.path = path;
    }

    public InventoryGUI createGUI() {
        FileManager config = new FileManager(plugin, FileType.CONFIG);
        String basePath = path + ".items.";

        String title = config.get(path + ".title");
        int size = config.getInt(path + ".size");


        if (config.contains(path + ".inventory_type")) {
            InventoryType type = InventoryType.valueOf(config.get(path + ".inventory_type"));
            inventory = Bukkit.createInventory(this, type, title);

        }else{
            inventory = Bukkit.createInventory(this, size, title);
        }

        try {
            for (String key : config.getSection(basePath).getKeys(false)) {
                int slot = config.getInt(basePath + key + ".slot");

                ItemStack itemStack = new ItemBuilder()
                        .fromConfig(config.toConfig(), basePath + key)
                        .toItemStack();


                if (config.contains(path + ".items." + key + ".slots")) {
                    for (Integer slots : config.toConfig().getIntegerList(path + ".items." + key + ".slots")) {
                        inventory.setItem(slots, itemStack);
                    }
                } else {
                    inventory.setItem(slot, itemStack);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Handle the click event
     *
     * @param event InventoryClickEvent
     */
    public abstract void onClick(InventoryClickEvent event);

    public abstract void init();

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void setItem(ItemStack itemStack, int slot) {
        inventory.setItem(slot, itemStack);
    }



    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
