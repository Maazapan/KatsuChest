package com.github.maazapan.katsuchest.api;

import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.types.PanelChest;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.awt.*;

public class ChestWrongPinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final PanelChest panelChest;
    private final Player player;

    private boolean cancelled;

    public ChestWrongPinEvent(PanelChest panelChest, Player player) {
        this.panelChest = panelChest;
        this.player = player;
        this.cancelled = false;
    }

    public PanelChest getPanelChest() {
        return panelChest;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}


