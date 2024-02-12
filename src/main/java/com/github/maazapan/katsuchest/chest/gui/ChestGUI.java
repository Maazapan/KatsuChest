package com.github.maazapan.katsuchest.chest.gui;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.chest.types.FriendChest;
import com.github.maazapan.katsuchest.chest.types.KeyChest;
import com.github.maazapan.katsuchest.chest.types.PanelChest;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import com.github.maazapan.katsuchest.utils.gui.InventoryGUI;
import com.github.maazapan.katsuchest.utils.itemstack.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ChestGUI extends InventoryGUI {

    private final KatsuChest plugin;
    private final Player player;

    private final CustomChest customChest;

    public ChestGUI(Player player, KatsuChest plugin, CustomChest customChest) {
        super(plugin, "config.inventory.chest-gui");
        this.customChest = customChest;
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    @SuppressWarnings("all")
    public void onClick(InventoryClickEvent event) {
        NBTItem nbtItem = new NBTItem(event.getCurrentItem());
        event.setCancelled(true);

        if (nbtItem.hasTag("katsu-chest-action")) {
            List<String> stringList = nbtItem.getObject("katsu-chest-action", List.class);
            FileManager config = new FileManager(plugin, FileType.CONFIG);
            FileManager message = new FileManager(plugin, FileType.MESSAGES);

            for (String action : stringList) {
                switch (action.toUpperCase()) {

                    // Open the friend GUI.
                    case "[FRIENDS]": {
                        new FriendGUI(player, plugin, (FriendChest) customChest).init();
                    }
                    break;

                    // Change password of the chest.
                    case "[PASSWORD]": {
                        ((PanelChest) customChest).hasPassword(false);
                        new PanelGUI(player, plugin, (PanelChest) customChest).init();
                        player.sendMessage(message.getWithPrefix("password-changed"));
                    }
                    break;

                    // Add key to the player inventory.
                    case "[KEY]": {
                        ChestManager chestManager = plugin.getChestManager();

                        int maxKeys = config.getInt("config.max-key-item-per-chest");
                        int amount = ((KeyChest) customChest).getKeyAmount();

                        if (amount >= maxKeys) {
                            player.sendMessage(message.getWithPrefix("max-keys"));
                            return;
                        }
                        chestManager.addChestKey(player, customChest);
                        ((KeyChest) customChest).setKeyAmount(amount + 1);
                        init();
                    }
                    break;

                    case "[INSERT]": {
                        customChest.setInsertContent(!customChest.isInsertContent());
                        init();
                    }
                    break;

                    case "[EXTRACT]": {
                        customChest.setExtractContent(!customChest.isExtractContent());
                        init();
                    }
                    break;

                    case "[LOCK]": {
                        customChest.setLocked(!customChest.isLocked());
                        init();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void init() {
        FileManager config = new FileManager(plugin, FileType.CONFIG);

        this.createGUI();
        // Add items to the GUI
        ItemBuilder lockItem = new ItemBuilder()
                .fromConfig(config.toConfig(), "config.inventory.chest-gui.lock-item")
                .replace("%status%", formatLocked());

        ItemBuilder extractItem = new ItemBuilder()
                .fromConfig(config.toConfig(), "config.inventory.chest-gui.extract-item")
                .replace("%status%", formatStatus(customChest.isExtractContent()));

        ItemBuilder insertItem = new ItemBuilder()
                .fromConfig(config.toConfig(), "config.inventory.chest-gui.insert-item")
                .replace("%status%", formatStatus(customChest.isInsertContent()));

        this.setItem(insertItem.toItemStack(), insertItem.getSlot());
        this.setItem(lockItem.toItemStack(), lockItem.getSlot());
        this.setItem(extractItem.toItemStack(), extractItem.getSlot());

        switch (customChest.getType()) {

            // Add key item to the GUI
            case KEY_CHEST: {
                int maxKeys = config.getInt("config.max-key-item-per-chest");
                int amount = maxKeys - ((KeyChest) customChest).getKeyAmount();

                ItemBuilder keyItem = new ItemBuilder()
                        .fromConfig(config.toConfig(), "config.inventory.chest-gui.key-item")
                        .replace("%amount%", String.valueOf(amount));

                this.setItem(keyItem.toItemStack(), keyItem.getSlot());
            }
            break;

            // Add password item to the GUI
            case PANEL_CHEST: {
                ItemBuilder passwordItem = new ItemBuilder()
                        .fromConfig(config.toConfig(), "config.inventory.chest-gui.password-item");
                this.setItem(passwordItem.toItemStack(), passwordItem.getSlot());
            }
            break;

            // Add friend item to the GUI
            case FRIEND_CHEST: {
                ItemBuilder friendItem = new ItemBuilder()
                        .fromConfig(config.toConfig(), "config.inventory.chest-gui.friend-item");
                this.setItem(friendItem.toItemStack(), friendItem.getSlot());
            }
            break;
        }
        this.open(player);
    }


    private String formatLocked() {
        return customChest.isLocked() ? "&c&lLOCKED" : "&A&lUNLOCKED";
    }

    private String formatStatus(boolean status) {
        return status ? "&a&lENABLED" : "&c&lDISABLED";
    }
}
