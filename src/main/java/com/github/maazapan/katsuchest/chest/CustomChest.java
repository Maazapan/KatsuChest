package com.github.maazapan.katsuchest.chest;

import com.github.maazapan.katsuchest.chest.enums.ChestType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class CustomChest {

    private final UUID owner;

    private boolean locked;

    private boolean redstone;
    private boolean hopper;

    private final ChestType type;
    private final UUID uuid;

    private Location location;
    private boolean open;

    public CustomChest(UUID uuid, UUID owner, ChestType type) {
        this.locked = true;
        this.redstone = false;
        this.hopper = false;
        this.open = false;
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
    
    public boolean isRedstone() {
        return redstone;
    }
    
    public void setRedstone(boolean redstone) {
        this.redstone = redstone;
    }
    
    public boolean isHopper() {
        return hopper;
    }
    
    public void setHopper(boolean hopper) {
        this.hopper = hopper;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    abstract public void open(Player player);

    abstract public void animation();

    abstract public boolean canOpen(Player player);

}
