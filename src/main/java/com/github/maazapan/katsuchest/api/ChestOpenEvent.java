package com.github.maazapan.katsuchest.api;

import com.github.maazapan.katsuchest.chest.CustomChest;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChestOpenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final CustomChest customChest;
    private final Player player;

    private boolean cancelled;
    private final Chest chest;

    public ChestOpenEvent(CustomChest customChest, Player player, Chest chest) {
        this.customChest = customChest;
        this.player = player;
        this.chest = chest;
        this.cancelled = false;
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

    public Chest getChest() {
        return chest;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}


