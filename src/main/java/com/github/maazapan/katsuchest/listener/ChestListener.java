package com.github.maazapan.katsuchest.listener;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.api.ChestOpenEvent;
import com.github.maazapan.katsuchest.api.ChestPlaceEvent;
import com.github.maazapan.katsuchest.api.ChestWrongPinEvent;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.gui.ChestGUI;
import com.github.maazapan.katsuchest.chest.gui.PanelGUI;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.chest.types.FriendChest;
import com.github.maazapan.katsuchest.chest.types.PanelChest;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

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
                ChestManager chestManager = plugin.getChestManager();
                chestManager.addChestKey(player, customChest);
            }
            break;

            // If the chest is a panel chest, then we open the panel GUI.
            case PANEL_CHEST: {
                new PanelGUI(player, plugin, (PanelChest) customChest).init();
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

        FileManager config = new FileManager(plugin, FileType.CONFIG);

        // If the player is sneaking and is the owner of the chest, then we open the chest GUI.
        if (player.isSneaking() && player.getUniqueId().equals(ownerUUID)) {
            KatsuUtils.parseSound(player, config.get("config.sound.open-chest"));
            new ChestGUI(player, plugin, customChest).init();

            event.setCancelled(true);
            return;
        }

        // If the custom chest is locked, then we check if the player can open it.
        if (customChest.isLocked()) {
            if (!customChest.canOpen(player)) {
                if (customChest.getType() == ChestType.KEY_CHEST) {
                    KatsuUtils.parseSound(player, config.get("config.sound.locked-chest"));

                } else if ( customChest.getType() == ChestType.FRIEND_CHEST) {
                    KatsuUtils.parseSound(player, config.get("config.sound.locked-friends"));
                }

                customChest.animation();
                event.setCancelled(true);
                return;
            }

            if (customChest.getType() == ChestType.PANEL_CHEST) {
                event.setCancelled(true);
                new PanelGUI(player, plugin, (PanelChest) customChest).init();
            }
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
                CustomChest customChest = chestManager.getCustomChest(block);
                if (!customChest.isInsertContent()) event.setCancelled(true);
            }
            return;
        }

        // If the destination is a hopper, cancel the event.
        if (destination.getType() == InventoryType.HOPPER) {
            if (source.getLocation() == null) return;
            Block block = source.getLocation().getBlock();

            if (chestManager.isCustomChest(block)) {
                CustomChest customChest = chestManager.getCustomChest(block);
                if (!customChest.isExtractContent()) event.setCancelled(true);
            }
        }
    }
}
