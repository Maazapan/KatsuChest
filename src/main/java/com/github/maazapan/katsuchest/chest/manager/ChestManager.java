package com.github.maazapan.katsuchest.chest.manager;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.api.ChestPlaceEvent;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.itemstack.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ChestManager {

    private final Map<UUID, CustomChest> chestMap = new HashMap<>();
    private final KatsuChest plugin;

    public ChestManager(KatsuChest plugin) {
        this.plugin = plugin;
    }

    /**
     * Create a custom chest.
     *
     * @param chestType ChestType
     * @return ItemStack Custom Chest
     */
    public ItemStack getCustomChestItem(ChestType chestType) {
        ItemStack itemStack = new ItemBuilder()
                .fromConfig(plugin.getConfig(), "config.custom-chest." + chestType + ".chest-item")
                .toItemStack();

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("katsu_chest_type", chestType.toString());
        nbtItem.applyNBT(itemStack);

        return itemStack;
    }

    /**
     * Place a custom chest.
     *
     * @param player    Player
     * @param location  Location
     * @param chestType ChestType
     */
    public void placeCustomChest(Player player, Location location, ChestType chestType) {
        ChestCreator chestCreator = new ChestCreator(plugin);
        CustomChest customChest = chestCreator.create(player, location, chestType);

        Chest chest = (Chest) location.getBlock().getState();

        // Call the event.
        ChestPlaceEvent chestPlaceEvent = new ChestPlaceEvent(customChest, player, chest);
        plugin.getServer().getPluginManager().callEvent(chestPlaceEvent);

        chestMap.put(customChest.getUUID(), customChest);
    }

    /**
     * Remove a custom chest.
     *
     * @param chestUUID Chest UUID
     * @param owner     Player UUID
     */
    public void removeChest(UUID chestUUID, UUID owner) {
        CustomChest customChest = chestMap.get(chestUUID);

        if (customChest == null || !customChest.getOwner().equals(owner)) return;
        // The List of armor stands near the chest.
        ArmorStand armorStand = KatsuUtils.chestArmorStand(customChest.getLocation(), chestUUID);

        if (armorStand != null) {
            armorStand.remove();
        }


        chestMap.remove(chestUUID);
    }


    /**
     * Remove a custom chest if the chest is not on the map.
     * And you can use if the chest is a bug.
     *
     * @param chestUUID Chest UUID
     * @param location  Block Location
     */
    public void removeChest(UUID chestUUID, Location location) {
        ArmorStand armorStand = KatsuUtils.chestArmorStand(location, chestUUID);

        if (armorStand != null) {
            armorStand.remove();
        }
    }


    /**
     * Check if the player is the owner of the chest.
     *
     * @param chestUUID Chest UUID
     * @param owner     Player UUID
     * @return boolean
     */
    public boolean isChestOwner(UUID chestUUID, UUID owner) {
        CustomChest customChest = chestMap.get(chestUUID);
        if (customChest == null) return false;
        return customChest.getOwner().equals(owner);
    }


    /**
     * Get the number of chests placed by the player.
     *
     * @param owner Player UUID
     * @return int
     */
    public int getAmountChestPlaced(UUID owner) {
        return (int) chestMap.values()
                .stream()
                .filter(chest -> chest.getOwner().equals(owner))
                .count();
    }

    /**
     * Get CustomChest from UUID
     *
     * @param uuid Custom Chest UUID
     * @return CustomChest
     */
    public CustomChest getCustomChest(UUID uuid) {
        if (uuid == null) return null;
        return chestMap.get(uuid);
    }


    /**
     * Get CustomChest from Block
     * @param block Block
     * @return CustomChest
     */
    public CustomChest getCustomChest(Block block) {
        if (block == null) return null;
        NBTBlock nbtBlock = new NBTBlock(block);
        return chestMap.get(nbtBlock.getData().getUUID("katsu_chest_uuid"));
    }

    /**
     * Checks if the block is a custom chest.
     *
     * @param block Block
     * @return boolean
     */
    public boolean isCustomChest(Block block) {
        return new NBTBlock(block).getData().hasTag("katsu_chest_uuid");
    }

    /**
     * Checks if the item is a custom chest.
     *
     * @param itemStack ItemStack
     * @return boolean
     */
    public boolean isCustomChest(ItemStack itemStack) {
        return new NBTItem(itemStack).hasTag("katsu_chest_type");
    }

    /**
     * Gets the chest type of the itemstack
     *
     * @param itemStack ItemStack
     * @return ChestType
     */

    public ChestType getChestType(ItemStack itemStack) {
        return ChestType.valueOf(new NBTItem(itemStack).getString("katsu_chest_type"));
    }

    /**
     * Gets the UUID of the custom chest.
     *
     * @param block Block
     * @return UUID
     */
    public UUID getCustomChestUUID(Block block) {
        return new NBTBlock(block).getData().getUUID("katsu_chest_uuid");
    }

    /**
     * Gets all owners of the chests.
     *
     * @return Set<UUID>
     */
    public Set<UUID> getChestOwners() {
        return chestMap.values().stream()
                .map(CustomChest::getOwner)
                .collect(Collectors.toSet());
    }

    /**
     * Gets all the chests of the owner.
     *
     * @param owner Player UUID
     * @return List<CustomChest>
     */
    public List<CustomChest> getChests(UUID owner) {
        return chestMap.values().stream()
                .filter(chest -> chest.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    /**
     * Add a custom chest to the map.
     *
     * @param chestUUID   UUID of the chest
     * @param customChest CustomChest
     */
    public void addChest(UUID chestUUID, CustomChest customChest) {
        chestMap.put(chestUUID, customChest);
    }

    /**
     * Gets all the chests.
     *
     * @return List<CustomChest>
     */
    public List<CustomChest> getChests() {
        return new ArrayList<>(chestMap.values());
    }

    /**
     * Checks if the chest exists.
     *
     * @param chestUUID Chest UUID
     * @return boolean
     */
    public boolean exists(UUID chestUUID) {
        return chestMap.containsKey(chestUUID);
    }
}
