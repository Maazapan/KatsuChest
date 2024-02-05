package com.github.maazapan.katsuchest.listener;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.api.ChestOpenEvent;
import com.github.maazapan.katsuchest.api.ChestPlaceEvent;
import com.github.maazapan.katsuchest.api.ChestWrongPinEvent;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.gui.FriendGUI;
import com.github.maazapan.katsuchest.chest.gui.PanelGUI;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.chest.types.FriendChest;
import com.github.maazapan.katsuchest.chest.types.PanelChest;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import com.github.maazapan.katsuchest.utils.itemstack.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ChestListener implements Listener {

    private final KatsuChest plugin;

    public ChestListener(KatsuChest plugin) {
        this.plugin = plugin;
    }

    /**
     * This method is called when a player enters a wrong pin.
     *
     * @param event ChestWrongPinEvent
     */
    @EventHandler
    public void onChestWrongPin(ChestWrongPinEvent event) {
        FileManager config = new FileManager(plugin, FileType.CONFIG);
        Player player = event.getPlayer();

        if (config.getBoolean("config.damage-wrong-pin.enable")) {
            double damage = config.getDouble("config.damage-wrong-pin.damage");
            player.damage(damage);
        }
    }


    /**
     * This method is called when a player places a block.
     * If the block is a custom chest, then we give the player a key.
     *
     * @param event ChestPlaceEvent
     */
    @EventHandler
    public void onChestPlace(ChestPlaceEvent event) {
        CustomChest customChest = event.getCustomChest();
        Player player = event.getPlayer();

        switch (customChest.getType()) {

            // Then we give the player a key.
            case KEY_CHEST: {
                ItemStack itemStack = new ItemBuilder()
                        .fromConfig(plugin.getConfig(), "config.custom-chest.KEY_CHEST.chest-key")
                        .toItemStack();

                NBTItem nbtItem = new NBTItem(itemStack);
                nbtItem.setUUID("katsu_chest_uuid", customChest.getUUID());
                nbtItem.applyNBT(itemStack);

                player.getInventory().addItem(itemStack);
            }
            break;

            // If the chest is a panel chest, then we open the panel GUI.
            case PANEL_CHEST: {
                new PanelGUI(player, plugin, (PanelChest) customChest, event.getChest()).init();
            }
            break;

            // Then we add the player to the friend's list.
            case FRIEND_CHEST: {
                ((FriendChest) customChest).getFriends().add(player.getUniqueId());
            }
            break;
        }
    }


    /**
     * Check if the player opens a custom chest
     *
     * @param event ChestOpenEvent
     */
    @EventHandler
    public void onChestInteract(ChestOpenEvent event) {
        CustomChest customChest = event.getCustomChest();
        Player player = event.getPlayer();
        UUID ownerUUID = customChest.getOwner();


        if (customChest.getType() == ChestType.PANEL_CHEST) {
            new PanelGUI(player, plugin, (PanelChest) customChest, event.getChest()).init();
            event.setCancelled(true);
            return;
        }

        if (!player.isSneaking() || !player.getUniqueId().equals(ownerUUID)) return;
        switch (customChest.getType()) {

            case FRIEND_CHEST: {
                new FriendGUI(player, plugin, (FriendChest) customChest).addPages().init();
                event.setCancelled(true);
            }
            break;


            case PANEL_CHEST: {

            }
            break;
        }
    }

    /**
     * This method is called when a block explodes.
     * If the block is a custom chest, then we cancel the event.
     *
     * @param event EntityExplodeEvent
     */
    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        ChestManager chestManager = plugin.getChestManager();
        event.blockList().removeIf(chestManager::isCustomChest);
    }

    /**
     * This method is called when an item is moved in a hopper inventory.
     * If the item is a custom chest, then we cancel the event.
     *
     * @param event InventoryMoveItemEvent
     */

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent event) {
        ChestManager chestManager = plugin.getChestManager();

        Inventory destination = event.getDestination();
        Inventory source = event.getSource();

        // If the destination is a chest, cancel the event.
        if (destination.getType() == InventoryType.CHEST) {
            if (destination.getLocation() == null) return;
            Block block = destination.getLocation().getBlock();

            if (chestManager.isCustomChest(block)) {
                event.setCancelled(true);
            }
            return;
        }

        // If the destination is a hopper, cancel the event.
        if (destination.getType() == InventoryType.HOPPER) {
            if (source.getLocation() == null) return;
            Block block = source.getLocation().getBlock();

            if (chestManager.isCustomChest(block)) {
                event.setCancelled(true);
            }
        }
    }
}
