package com.github.maazapan.katsuchest.chest.types;

import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PanelChest extends CustomChest {

    private String password;

    public PanelChest(UUID uuid, UUID owner) {
        super(uuid, owner, ChestType.PANEL_CHEST);
    }


    @Override
    public void open(Player player) {

    }


    @Override
    public boolean canOpen(Player player) {
        return false;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
