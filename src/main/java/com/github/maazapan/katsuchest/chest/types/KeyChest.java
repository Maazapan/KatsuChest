package com.github.maazapan.katsuchest.chest.types;

import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class KeyChest extends CustomChest {

    private boolean requireKey;

    public KeyChest(UUID uuid, UUID owner) {
        super(uuid, owner, ChestType.KEY_CHEST);
    }

    @Override
    public void open(Player player) {

    }

    @Override
    public boolean canOpen(Player player) {
        ItemStack itemStack = player.getInventory().getItemInHand();

        if (itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            UUID keyUUID = nbtItem.getUUID("katsu_chest_uuid");

            if (keyUUID == null) return false;
            return keyUUID.equals(getUUID());
        }
        return false;
    }
}
