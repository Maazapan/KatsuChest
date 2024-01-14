package com.github.maazapan.katsuchest.chest;

import com.github.maazapan.katsuchest.chest.enums.ChestType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public abstract class CustomChest {

    private final UUID owner;

    private boolean locked;

    private final ChestType type;
    private final UUID uuid;
    private Location location;

    public CustomChest(UUID uuid, UUID owner, ChestType type) {
        this.locked = true;
        this.owner = owner;
        this.type = type;
        this.uuid = uuid;
    }

    public UUID getOwner() {
        return owner;
    }

    public ChestType getType() {
        return type;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    abstract public void open(Player player);

    abstract public boolean canOpen(Player player);
}
