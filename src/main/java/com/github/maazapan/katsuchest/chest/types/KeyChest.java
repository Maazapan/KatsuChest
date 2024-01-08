package com.github.maazapan.katsuchest.chest.types;

import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class KeyChest extends CustomChest {

    private ItemStack itemKey;

    public KeyChest(UUID uuid, UUID owner) {
        super(uuid, owner, ChestType.KEY_CHEST);
    }

    @Override
    public void open(Player player) {

    }

    @Override
    public ItemStack getBlock() {
        return null;
    }

    @Override
    public boolean canOpen(Player player) {



        return false;
    }

    public ItemStack getItemKey() {
        return itemKey;
    }
}
