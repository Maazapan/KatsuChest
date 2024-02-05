package com.github.maazapan.katsuchest.chest.types;

import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PanelChest extends CustomChest {

    private String password = "123";
    private boolean hasPassword = false;
    public PanelChest(UUID uuid, UUID owner) {
        super(uuid, owner, ChestType.PANEL_CHEST);
    }


    @Override
    public void open(Player player) {

    }

    @Override
    public void animation() {
        ArmorStand armorStand = KatsuUtils.chestArmorStand(getLocation(), getUUID());
        if (armorStand == null) return;
        Vector distance = armorStand.getEyeLocation().getDirection().multiply(0.3);
        Location location = armorStand.getLocation().add(0,0.8,0).add(distance);

        Vector rotated = KatsuUtils.rotateAroundY(Math.toRadians(-90), armorStand.getEyeLocation().getDirection().multiply(0.12)).setY(0);
        location.add(rotated);

        armorStand.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,  new Particle.DustOptions(Color.RED, 1));
    }

    @Override
    public boolean canOpen(Player player) {
        return true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasPassword() {
        return hasPassword;
    }

    public void hasPassword(boolean enablePassword) {
        this.hasPassword = enablePassword;
    }
}
