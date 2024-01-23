package com.github.maazapan.katsuchest.chest.gui;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.utils.gui.InventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PanelGUI extends InventoryGUI {

    private final Player player;
    private CustomChest customChest;

    public PanelGUI(Player player, KatsuChest plugin) {
        super(plugin, "config.inventory.panel-chest");
        this.player = player;
    }

    @Override
    public void onClick(InventoryClickEvent event) {

    }

    @Override
    public void init() {
        this.createGUI().open(player);
    }
}
