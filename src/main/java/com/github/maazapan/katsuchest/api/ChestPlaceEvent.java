package com.github.maazapan.katsuchest.api;

import com.github.maazapan.katsuchest.chest.CustomChest;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChestPlaceEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CustomChest customChest;
    private final Player player;

    private Chest chest;

    public ChestPlaceEvent(CustomChest customChest, Player player, Chest chest) {
        this.customChest = customChest;
        this.player = player;
        this.chest = chest;
    }

    public CustomChest getCustomChest() {
        return customChest;
    }

    public Player getPlayer() {
        return player;
    }

    public Chest getChest() {
        return chest;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
