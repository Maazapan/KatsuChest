package com.github.maazapan.katsuchest.manager.loader;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.chest.types.FriendChest;
import com.github.maazapan.katsuchest.chest.types.KeyChest;
import com.github.maazapan.katsuchest.chest.types.PanelChest;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChestLoader {

    private final KatsuChest plugin;

    public ChestLoader(KatsuChest plugin) {
        this.plugin = plugin;
    }


    /**
     * Save all custom chests to the files.
     */
    public void save() {
        ChestManager chestManager = plugin.getChestManager();

        try {
            // Create the data folder if it doesn't exist.
            if (!Files.exists(Paths.get(plugin.getDataFolder() + "/data"))) {
                Files.createDirectory(Paths.get(plugin.getDataFolder() + "/data"));
            }

            // Delete all files in data folder.
            File[] listFiles = new File(plugin.getDataFolder() + "/data").listFiles();

            if (listFiles != null) {
                Arrays.stream(listFiles).forEach(File::delete);
            }

            for (UUID ownerUUID : chestManager.getChestOwners()) {
                File file = new File(plugin.getDataFolder() + "/data/" + ownerUUID + ".yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                for (CustomChest chest : chestManager.getChests(ownerUUID)) {
                    UUID uuid = chest.getUUID();

                    config.set(uuid + ".type", chest.getType().toString());
                    config.set(uuid + ".locked", chest.isLocked());

                    // Save chest location.
                    if (chest.getLocation() != null) {
                        config.set(uuid + ".location", KatsuUtils.locationToString(chest.getLocation()));
                    }

                    // Save chest password.
                    if (chest.getType() == ChestType.PANEL_CHEST) {
                        PanelChest panelChest = (PanelChest) chest;
                        String password = Base64.getEncoder().encodeToString(panelChest.getPassword().getBytes());
                        config.set(uuid + ".password", password);
                    }

                    // Save chest friends.
                    if (chest.getType() == ChestType.FRIEND_CHEST) {
                        FriendChest friendChest = (FriendChest) chest;
                        config.set(uuid + ".friends", friendChest.getFriends());
                    }
                    config.save(file);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Load all custom chests from the files.
     */
    @SuppressWarnings("all")
    public void load() {
        ChestManager chestManager = plugin.getChestManager();

        try {
            // Check if the data folder exists.
            if (!Files.exists(Paths.get(plugin.getDataFolder() + "/data"))) return;
            File[] files = new File(plugin.getDataFolder() + "/data").listFiles();

            if (files == null) return;
            for (File file : files) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                UUID ownerUUID = UUID.fromString(file.getName().replace(".yml", ""));

                for (String key : config.getKeys(false)) {
                    UUID chestUUID = UUID.fromString(key);

                    boolean locked = config.getBoolean(chestUUID + ".locked");
                    ChestType chestType = ChestType.valueOf(config.getString(chestUUID + ".type"));

                    CustomChest customChest = new KeyChest(chestUUID, ownerUUID);
                    customChest.setLocked(locked);

                    // Check if the chest has a location.
                    if (config.isSet(chestUUID + ".location")) {
                        String locationString = config.getString(chestUUID + ".location");
                        customChest.setLocation(KatsuUtils.stringToLocation(locationString));
                    }

                    switch (chestType) {
                        /*
                         * If the chest is a panel chest, then we load the password.
                         */
                        case PANEL_CHEST: {
                            PanelChest panelChest = (PanelChest) customChest;

                            if (config.isSet(chestUUID + ".password")) {
                                String password = config.getString(chestUUID + ".password");
                                panelChest.setPassword(new String(Base64.getDecoder().decode(password)));
                            }
                            chestManager.addChest(chestUUID, panelChest);
                        }
                        break;

                        /*
                         * If the chest is a friend chest, then we load the friends.
                         */
                        case FRIEND_CHEST: {
                            FriendChest friendChest = (FriendChest) customChest;

                            if (config.isSet(chestUUID + ".friends")) {
                                List<UUID> friends = config.getStringList(chestUUID + ".friends")
                                        .stream().map(UUID::fromString).collect(Collectors.toList());
                                friendChest.getFriends().addAll(friends);
                            }
                            chestManager.addChest(chestUUID, friendChest);
                        }
                        break;

                        default:
                            chestManager.addChest(chestUUID, customChest);
                            break;
                    }
                }
            }

            plugin.getLogger().info("Loaded " + chestManager.getChests().size() + " custom chests.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
