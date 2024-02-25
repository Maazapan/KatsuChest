package com.github.maazapan.katsuchest.listener;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.utils.gui.InventoryGUI;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryListener implements Listener {

    private final KatsuChest plugin;

    public InventoryListener(KatsuChest plugin) {
        this.plugin = plugin;
    }


    /**
     * This method is called when a player clicks on a chest panel.
     *
     * @param event InventoryClickEvent
     */
    @EventHandler
    @SuppressWarnings("all")
    public void onClick(InventoryClickEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack != null && itemStack.getType() != Material.AIR) {
            if (inventoryHolder instanceof InventoryGUI) {
                InventoryGUI inventoryGUI = (InventoryGUI) inventoryHolder;
                NBTItem nbtItem = new NBTItem(itemStack);

                if (nbtItem.hasTag("katsu-chest-item")) event.setCancelled(true);

                // Handle the click event.
                inventoryGUI.onClick(event);

                if (nbtItem.hasTag("katsu-chest-action")) {
                    List<String> actions = nbtItem.getObject("katsu-chest-action", List.class);
                    Player player = (Player) event.getWhoClicked();

                    for (String action : actions) {
                        String[] actionSplit = action.split(": ");

                        switch (actionSplit[0]) {
                            // Close inventory.
                            case "[CLOSE]": {
                                player.closeInventory();
                            }
                            break;

                            // Execute command as player.
                            case "[COMMAND]": {
                                player.performCommand(actionSplit[1]);
                            }
                            break;

                            // Execute command as console.
                            case "[CONSOLE]": {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actionSplit[1]);
                            }
                            break;


                            // Play sound at player.
                            case "[SOUND]": {
                                String[] b = actionSplit[1].split(";");
                                player.playSound(player.getLocation(), Sound.valueOf(b[0]), Float.parseFloat(b[1]), Float.parseFloat(b[2]));
                            }
                            break;

                            case "[NEXT_PAGE]":
                            case "[PREVIOUS_PAGE]":
                            case "[NUMBER]":
                            case "[RESET]":
                            case "[CONFIRM]":
                            case "[LOCK]":
                            case "[EXTRACT]":
                            case "[INSERT]":
                            case "[BACK]":
                            case "[KEY]":
                            case "[FRIENDS]":
                            case "[PASSWORD]":
                                break;

                            default:
                                plugin.getLogger().warning("Error action " + actionSplit[0] + " not found.");
                                break;
                        }
                    }
                }
            }
        }
    }
}
