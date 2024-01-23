package com.github.maazapan.katsuchest.chest.gui;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.utils.gui.InventoryGUI;
import com.github.maazapan.katsuchest.utils.gui.pages.PlayerPage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class FriendGUI extends InventoryGUI {

    private KatsuChest plugin;
    private final Player player;

    private final Map<UUID, PlayerPage> playerPages;

    public FriendGUI(Player player, KatsuChest plugin) {
        super(plugin, "config.inventory.friends-chest");
        this.playerPages = new HashMap<>();
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void onClick(InventoryClickEvent event) {

    }

    @Override
    public void init() {
        this.createGUI();

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        PlayerPage page = playerPages.get(player.getUniqueId());

        int a = 0;

        for (int i = 36 * (page.getPage() - 1); i < players.size(); i++) {


            a++;

            if (a > 35) {
                break;
            }
        }
        this.open(player);
    }

    public int getMaxPages() {
        int size = Bukkit.getOnlinePlayers().size();

        if (size % 36 == 0) {
            return size / 36;
        }
        return (size / 36) + 1;
    }
}
