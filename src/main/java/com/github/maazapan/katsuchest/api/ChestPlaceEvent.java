package com.github.maazapan.katsuchest.api;

import com.github.maazapan.katsuchest.chest.CustomChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChestPlaceEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CustomChest customChest;
    private final Player player;

    public ChestPlaceEvent(CustomChest customChest, Player player) {
        this.customChest = customChest;
        this.player = player;
    }


    public CustomChest getCustomChest() {
        return customChest;
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
