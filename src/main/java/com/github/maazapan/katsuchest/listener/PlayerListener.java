package com.github.maazapan.katsuchest.listener;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
            UUID chestUUID = chestManager.getCustomChestUUID(block);
            CustomChest customChest = chestManager.getCustomChest(chestUUID);

            FileManager config = new FileManager(plugin, FileType.CONFIG);

            if (customChest == null) return;
            // Check if the player can open the chest.
            if (!customChest.canOpen(player)) {
                KatsuUtils.parseSound(player, config.get("config.sound.locked-chest"));
                event.setCancelled(true);

                // Animate the chest.
                customChest.animation();
                return;
            }
            customChest.open(player);
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

        if (event.isCancelled() || itemStack.getType() == Material.AIR) return;
        ChestManager chestManager = plugin.getChestManager();

        // If the item is a custom chest, then we create a new custom chest.
        if (chestManager.isCustomChest(itemStack)) {
            FileManager fileManager = new FileManager(plugin);
            int maxChests = fileManager.getInt("config.max-place-chest", FileType.CONFIG);

            if (chestManager.getAmountChestPlaced(player.getUniqueId()) >= maxChests) {
                player.sendMessage(fileManager.get("max-chest-placed", FileType.MESSAGES));
                event.setCancelled(true);
                return;
            }
            ChestType chestType = chestManager.getChestType(itemStack);
            Location location = event.getBlock().getLocation();

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

            if (chestManager.isChestOwner(chestUUID, player.getUniqueId())) {
                chestManager.removeChest(chestUUID, player.getUniqueId());
                CustomChest customChest = chestManager.getCustomChest(chestUUID);

                // Drop the custom chest item.
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    ItemStack itemStack = chestManager.getCustomChestItem(customChest.getType());
                    player.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                }

            }
        }
    }
}
