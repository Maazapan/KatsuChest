package com.github.maazapan.katsuchest.chest.types;

import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FriendChest extends CustomChest {

    public FriendChest(UUID uuid, UUID owner) {
        super(uuid, owner, ChestType.FRIEND_CHEST);
    }

    @Override
    public void open(Player player) {

    }


    @Override
    public boolean canOpen(Player player) {
        return false;
    }
}
