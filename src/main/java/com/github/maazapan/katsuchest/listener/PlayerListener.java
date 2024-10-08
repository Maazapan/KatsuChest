package com.github.maazapan.katsuchest.listener;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.api.ChestOpenEvent;
import com.github.maazapan.katsuchest.api.integrations.IntegrationManager;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final KatsuChest plugin;

    public PlayerListener(KatsuChest plugin) {
        this.plugin = plugin;
    }

    /**
     * This method is called when a player interacts with a block.
     * If the block is a custom chest, then we check if the player can open it.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || block == null) return;
        ChestManager chestManager = plugin.getChestManager();

        // If the block is a custom chest, then we check if the player can open it.
        if (chestManager.isCustomChest(block)) {
            IntegrationManager integrationManager = plugin.getIntegrationManager();

            if (!integrationManager.canUse(player, block.getLocation())) {
                event.setCancelled(true);
                return;
            }
            UUID chestUUID = chestManager.getCustomChestUUID(block);
            CustomChest customChest = chestManager.getCustomChest(chestUUID);

            // If the custom chest is null, not continue.
            if (customChest == null) return;
            ChestOpenEvent openEvent = new ChestOpenEvent(customChest, player, (Chest) block.getState());
            Bukkit.getPluginManager().callEvent(openEvent);

            event.setCancelled(openEvent.isCancelled());
        }
    }

    /**
     * This method is called when a player places a block.
     * If the block is a custom chest, create a new custom chest.
     *
     * @param event BlockPlaceEvent
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        Player player = event.getPlayer();

        Block block = event.getBlock();

        if (event.isCancelled() || itemStack.getType() == Material.AIR) return;
        ChestManager chestManager = plugin.getChestManager();
        Location location = event.getBlock().getLocation();

        if (block.getType() == Material.CHEST) {
            if (!chestManager.canPlaceChest(block.getLocation())) {
                event.setCancelled(true);
            }
            return;
        }

        // If the item is a custom chest, then we create a new custom chest.
        if (chestManager.isCustomChest(itemStack)) {
            IntegrationManager integrationManager = plugin.getIntegrationManager();

            if (!integrationManager.canUse(player, location)) {
                event.setCancelled(true);
                return;
            }

            FileManager config = new FileManager(plugin);
            int maxChests = config.getInt("config.max-place-chest", FileType.CONFIG);

            if (chestManager.getAmountChestPlaced(player.getUniqueId()) >= maxChests) {
                player.sendMessage(config.get("max-chest-placed", FileType.MESSAGES));
                event.setCancelled(true);
                return;
            }

            ChestType chestType = chestManager.getChestType(itemStack);
            chestManager.placeCustomChest(player, location, chestType);

            if (player.getGameMode() != GameMode.CREATIVE) player.getInventory().remove(itemStack);
        }
    }

    /**
     * This method is called when a player breaks a block.
     * If the block is a custom chest, then we remove it from the map.
     *
     * @param event BlockBreakEvent
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() != Material.CHEST) return;
        ChestManager chestManager = plugin.getChestManager();

        // If the block is a custom chest, then we remove it from the map.
        if (chestManager.isCustomChest(block)) {
            UUID chestUUID = chestManager.getCustomChestUUID(block);
            UUID ownerUUID = chestManager.getCustomChestOwner(block);

            // Check if the player can break the chest.
            IntegrationManager integrationManager = plugin.getIntegrationManager();

            if (!integrationManager.canUse(player, block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            // Check if the chest is bugged remove it.
            if (!chestManager.exists(chestUUID)) {
                chestManager.removeChest(chestUUID, block.getLocation());
                return;
            }

            if (!player.hasPermission("katsuchest.break.admin") && !chestManager.isChestOwner(chestUUID, player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            CustomChest customChest = chestManager.getCustomChest(chestUUID);
            chestManager.removeChest(chestUUID, ownerUUID);

            // Drop the custom chest item.
            if (player.getGameMode() == GameMode.SURVIVAL) {
                ItemStack itemStack = chestManager.getCustomChestItem(customChest.getType());
                player.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                event.setDropItems(false);

                Chest chest = (Chest) block.getState();

                for (ItemStack drop : chest.getBlockInventory()) {
                    if (drop == null || drop.getType() == Material.AIR) continue;

                    player.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
            }
        }
    }
}
