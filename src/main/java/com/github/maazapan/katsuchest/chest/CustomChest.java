package com.github.maazapan.katsuchest.chest;

import com.github.maazapan.katsuchest.chest.enums.ChestType;
import de.tr7zw.changeme.nbtapi.utils.UUIDUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public abstract class CustomChest {

    private List<UUID> friends;
    private final UUID owner;

    private boolean locked;

    private final ChestType type;
    private final UUID uuid;

    public CustomChest(UUID uuid, UUID owner, ChestType type) {
        this.locked = true;
        this.owner = owner;
        this.type = type;
        this.uuid = uuid;
    }

    public List<UUID> getFriends() {
        return friends;
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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    abstract public void open(Player player);

    abstract public ItemStack getBlock();
    abstract public boolean canOpen(Player player);
}
