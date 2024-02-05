package com.github.maazapan.katsuchest.manager.loader;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.chest.types.FriendChest;
import com.github.maazapan.katsuchest.chest.types.KeyChest;
import com.github.maazapan.katsuchest.chest.types.PanelChest;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
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
    private boolean crashed = false;

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
                List<CustomChest> customChests = chestManager.getChests(ownerUUID);

                if (customChests.isEmpty()) continue;
                File file = new File(plugin.getDataFolder() + "/data/" + ownerUUID + ".yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                for (CustomChest chest : customChests) {
                    UUID uuid = chest.getUUID();

                    config.set(uuid + ".type", chest.getType().toString());
                    config.set(uuid + ".locked", chest.isLocked());

                    config.set(uuid + ".redstone", chest.isRedstone());
                    config.set(uuid + ".hopper", chest.isHopper());

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
                        List<String> friends = friendChest.getFriends().stream()
                                .map(UUID::toString)
                                .collect(Collectors.toList());

                        config.set(uuid + ".friends", friends);
                    }

                    if (chest.getType() == ChestType.KEY_CHEST) {
                        KeyChest keyChest = (KeyChest) chest;
                        config.set(uuid + ".require-key", keyChest.isRequireKey());
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

                    ChestType chestType = ChestType.valueOf(config.getString(chestUUID + ".type"));
                    CustomChest customChest = null;

                    switch (chestType) {
                        /*
                         * If the chest is a key chest, then we load the require key.
                         */
                        case KEY_CHEST: {
                            customChest = new KeyChest(chestUUID, ownerUUID);

                            if (config.isSet(chestUUID + ".require-key")) {
                                boolean requireKey = config.getBoolean(chestUUID + ".require-key");
                                ((KeyChest) customChest).setRequireKey(requireKey);
                            }
                        }
                        break;
                        /*
                         * If the chest is a panel chest, then we load the password.
                         */
                        case PANEL_CHEST: {
                            customChest = new PanelChest(chestUUID, ownerUUID);

                            if (config.isSet(chestUUID + ".password")) {
                                String password = config.getString(chestUUID + ".password");
                                ((PanelChest) customChest).setPassword(new String(Base64.getDecoder().decode(password)));
                                ((PanelChest) customChest).hasPassword(true);
                            }
                        }
                        break;
                        /*
                         * If the chest is a friend chest, then we load the friends.
                         */
                        case FRIEND_CHEST: {
                            customChest = new FriendChest(chestUUID, ownerUUID);

                            if (config.isSet(chestUUID + ".friends")) {
                                List<UUID> friends = config.getStringList(chestUUID + ".friends")
                                        .stream().map(UUID::fromString).collect(Collectors.toList());
                                ((FriendChest) customChest).getFriends().addAll(friends);
                            }
                        }
                        break;
                    }

                    boolean locked = config.getBoolean(chestUUID + ".locked");
                    boolean redstone = config.getBoolean(chestUUID + ".redstone");
                    boolean hopper = config.getBoolean(chestUUID + ".hopper");

                    customChest.setLocked(locked);
                    customChest.setRedstone(redstone);
                    customChest.setHopper(hopper);

                    // Check if the chest has a location.
                    if (config.isSet(chestUUID + ".location")) {
                        String locationString = config.getString(chestUUID + ".location");
                        customChest.setLocation(KatsuUtils.stringToLocation(locationString));
                    }

                    // Add the chest to the chest manager.
                    chestManager.addChest(chestUUID, customChest);
                }
            }

            plugin.getLogger().info("Success loading chests.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
