package com.github.maazapan.katsuchest.chest.manager;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.types.FriendChest;
import com.github.maazapan.katsuchest.chest.types.KeyChest;
import com.github.maazapan.katsuchest.chest.types.PanelChest;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import com.github.maazapan.katsuchest.utils.itemstack.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChestCreator {

    private final KatsuChest plugin;

    public ChestCreator(KatsuChest plugin) {
        this.plugin = plugin;
    }

    /**
     * Create a custom chest. (ChestType, Location)
     *
     * @param player    Player
     * @param location  Location
     * @param chestType ChestType
     * @return CustomChest
     */
    @SuppressWarnings("all")
    public CustomChest create(Player player, Location location, ChestType chestType) {
        FileManager config = new FileManager(plugin, FileType.CONFIG);
        location.getBlock().setType(Material.CHEST);

        // Place the chest.
        CustomChest customChest = customChest(chestType, player.getUniqueId());
        this.placeChestBlock(location, player);

        // Place the armor stand.
        ArmorStand armorStand = this.createArmorStand(location, player.getFacing().getOppositeFace());
        ItemBuilder itemBuilder = new ItemBuilder().fromConfig(config.toConfig(), "config.custom-chest." + chestType);

        if (config.getBoolean("config.custom-chest." + chestType + ".skull_owner")) {
            itemBuilder.setSkullOwner(player.getName());
        }

        armorStand.setHelmet(itemBuilder.toItemStack());
        customChest.setLocation(location);

        NBTBlock nbtBlock = new NBTBlock(location.getBlock());
        nbtBlock.getData().setString("katsu_chest_type", chestType.toString());
        nbtBlock.getData().setUUID("katsu_chest_uuid", customChest.getUUID());

        NBTEntity nbtEntity = new NBTEntity(armorStand);
        nbtEntity.getPersistentDataContainer().setUUID("katsu_chest_uuid", customChest.getUUID());

        return customChest;
    }


    /**
     * Create a custom chest. (ChestType, Location)
     *
     * @param chestType ChestType
     * @param ownerUUID Owner UUID
     * @return CustomChest
     */
    private CustomChest customChest(ChestType chestType, UUID ownerUUID) {
        UUID chestUUID = UUID.randomUUID();

        switch (chestType) {
            case KEY_CHEST:
                return new KeyChest(chestUUID, ownerUUID);
            case PANEL_CHEST:
                return new PanelChest(chestUUID, ownerUUID);
            case FRIEND_CHEST:
                return new FriendChest(chestUUID, ownerUUID);
        }
        return null;
    }

    /**
     * Place a chest block.
     *
     * @param location Location
     * @param player   Player
     */
    private void placeChestBlock(Location location, Player player) {
        location.getBlock().setType(Material.CHEST);

        // Place the chest.
        BlockFace facing = player.getFacing().getOppositeFace();

        Chest chest = (Chest) location.getBlock().getState();
        Directional directional = (Directional) chest.getBlockData();
        directional.setFacing(facing);

        chest.setBlockData(directional);
        chest.update();
    }

    /**
     * Create an armor stand.
     *
     * @param location Location
     * @return ArmorStand
     */
    private ArmorStand createArmorStand(Location location, BlockFace facing) {
        Location standLocation = location.clone().add(0.5, -0.45, 0.5);

        standLocation.setYaw(KatsuUtils.getYawBlockFace(facing));
        standLocation.add(standLocation.getDirection().multiply(0.3));

        return location.getWorld().spawn(standLocation, ArmorStand.class, (ArmorStand armorStand) -> {
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);
            armorStand.setVisible(false);
        });
    }
}
