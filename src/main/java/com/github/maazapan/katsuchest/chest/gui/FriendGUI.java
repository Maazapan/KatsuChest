package com.github.maazapan.katsuchest.chest.gui;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.types.FriendChest;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import com.github.maazapan.katsuchest.utils.gui.InventoryGUI;
import com.github.maazapan.katsuchest.utils.gui.pages.PlayerPage;
import com.github.maazapan.katsuchest.utils.itemstack.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendGUI extends InventoryGUI {

    private final KatsuChest plugin;

    private final Player player;
    private final FriendChest friendChest;

    private final Map<UUID, PlayerPage> pagesMap;

    public FriendGUI(Player player, KatsuChest plugin, FriendChest friendChest) {
        super(plugin, "config.inventory.friends-chest");
        this.pagesMap = new HashMap<>();
        this.friendChest = friendChest;
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    @SuppressWarnings("all")
    public void onClick(InventoryClickEvent event) {
        NBTItem nbtItem = new NBTItem(event.getCurrentItem());
        event.setCancelled(true);

        // Check if the item has a player head.
        if (nbtItem.hasTag("katsu_player_uuid")) {
            FileManager messages = new FileManager(plugin, FileType.MESSAGES);
            FileManager config = new FileManager(plugin, FileType.CONFIG);

            UUID uuid = nbtItem.getUUID("katsu_player_uuid");

            if (friendChest.getFriends().contains(uuid)) {
                player.sendMessage(messages.getWithPrefix("remove-friend").replace("%player_name%", Bukkit.getOfflinePlayer(uuid).getName()));
                KatsuUtils.parseSound(player, config.get("config.sound.remove-friends"));
                friendChest.getFriends().remove(uuid);

            } else {
                player.sendMessage(messages.getWithPrefix("add-friend").replace("%player_name%", Bukkit.getOfflinePlayer(uuid).getName()));
                KatsuUtils.parseSound(player, config.get("config.sound.add-friends"));
                friendChest.getFriends().add(uuid);
            }
            init();
        }

        // Check if the item has actions.
        if (nbtItem.hasTag("katsu-chest-action")) {
            List<String> actions = nbtItem.getObject("katsu-chest-action", List.class);
            PlayerPage page = pagesMap.get(player.getUniqueId());

            for (String action : actions) {
                switch (action) {

                    // Open a chest gui.
                    case "[BACK]": {
                        new ChestGUI(player, plugin, friendChest).init();
                    }
                    break;

                    // Open a next page.
                    case "[NEXT_PAGE]": {
                        if (page.getPage() < getMaxPages()) {
                            page.setPage(page.getPage() + 1);
                            init();
                        }
                    }
                    break;

                    // Open a previous page.
                    case "[PREVIOUS_PAGE]": {
                        if (page.getPage() > 1) {
                            page.setPage(page.getPage() - 1);
                            init();
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void init() {
        this.createGUI();

        PlayerPage page = pagesMap.get(player.getUniqueId());
        Set<UUID> uuids = Stream.concat(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId), friendChest.getFriends().stream())
                .filter(uuid -> !player.getUniqueId().equals(uuid)).collect(Collectors.toSet());

        // Check if the list is empty.
        if (uuids.isEmpty()) {
            ItemBuilder itemBuilder = new ItemBuilder()
                    .fromConfig(plugin.getConfig(), "config.inventory.friends-chest.empty-players");
            this.setItem(itemBuilder.toItemStack(), itemBuilder.getSlot());
        }

        int a = 10;

        for (int i = 26 * (page.getPage() - 1); i < uuids.size(); i++) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuids.toArray(new UUID[0])[i]);
            ItemBuilder itemBuilder = new ItemBuilder(Material.PLAYER_HEAD);

            if (friendChest.getFriends().contains(offlinePlayer.getUniqueId())) {
                itemBuilder.fromConfig(plugin.getConfig(), "config.inventory.friends-chest.friend-item");

            } else {
                itemBuilder.fromConfig(plugin.getConfig(), "config.inventory.friends-chest.not-friend-item");
            }

            itemBuilder.replace("%player_name%", offlinePlayer.getName())
                    .setSkullOwner(offlinePlayer.getName())
                    .setNBT("katsu_player_uuid", offlinePlayer.getUniqueId());

            getInventory().addItem(itemBuilder.toItemStack());
            a++;
            if (a >= 38) break;
        }
        this.open(player);
    }

    /**
     * Get the max pages to have a menu.
     *
     * @return int
     */
    public int getMaxPages() {
        int size = Bukkit.getOnlinePlayers().size();

        if (size % 26 == 0) {
            return size / 26;
        }
        return (size / 26) + 1;
    }

    /**
     * Add pages to the map.
     *
     * @return FriendGUI
     */
    public FriendGUI addPages() {
        pagesMap.put(player.getUniqueId(), new PlayerPage(player.getUniqueId(), 1));
        return this;
    }
}
