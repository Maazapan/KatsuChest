package com.github.maazapan.katsuchest.chest.gui;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.types.FriendChest;
import com.github.maazapan.katsuchest.utils.gui.InventoryGUI;
import com.github.maazapan.katsuchest.utils.gui.pages.PlayerPage;
import com.github.maazapan.katsuchest.utils.itemstack.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FriendGUI extends InventoryGUI {

    private KatsuChest plugin;

    private final Player player;
    private FriendChest friendChest;

    private final Map<UUID, PlayerPage> pagesMap;

    public FriendGUI(Player player, KatsuChest plugin, FriendChest friendChest) {
        super(plugin, "config.inventory.friends-chest");
        this.pagesMap = new HashMap<>();
        this.friendChest = friendChest;
        this.plugin = plugin;
        this.player = player;
    }

    @SuppressWarnings("all")
    @Override
    public void onClick(InventoryClickEvent event) {
        NBTItem nbtItem = new NBTItem(event.getCurrentItem());
        event.setCancelled(true);

        if (nbtItem.hasTag("katsu-chest-action")) {
            List<String> actions = nbtItem.getObject("katsu-chest-action", List.class);
            PlayerPage page = pagesMap.get(player.getUniqueId());

            for (String action : actions) {
                switch (action) {
                    case "[NEXT_PAGE]": {
                        if (page.getPage() < getMaxPages()) {
                            page.setPage(page.getPage() + 1);
                            init();
                        }
                    }
                    break;

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

        List<UUID> uuids = Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toList());

        PlayerPage page = pagesMap.get(player.getUniqueId());

        int a = 10;

        for (int i = 26 * (page.getPage() - 1); i < uuids.size(); i++) {
            UUID uuid = uuids.get(i);

            ItemBuilder itemBuilder = new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullOwner(uuid);

            if (friendChest.getFriends().contains(uuid)) {
                itemBuilder.fromConfig(plugin.getConfig(), "config.inventory.friends-chest.friend-item");
            } else {
                itemBuilder.fromConfig(plugin.getConfig(), "config.inventory.friends-chest.not-friend-item");
            }

            ItemStack itemStack = itemBuilder.toItemStack();
            NBTItem nbtItem = new NBTItem(itemStack);

            nbtItem.setUUID("katsu-player-uuid", uuid);
            nbtItem.applyNBT(itemStack);

            getInventory().addItem(itemStack);

            a++;

            if (a >= 38) break;
        }
        this.open(player);
    }

    public int getMaxPages() {
        int size = Bukkit.getOnlinePlayers().size();

        if (size % 26 == 0) {
            return size / 26;
        }
        return (size / 26) + 1;
    }

    public FriendGUI addPages() {
        pagesMap.put(player.getUniqueId(), new PlayerPage(player.getUniqueId(), 1));
        return this;
    }

}
