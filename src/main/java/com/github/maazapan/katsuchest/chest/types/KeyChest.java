package com.github.maazapan.katsuchest.chest.types;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.task.KatsuTask;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.UUID;

public class KeyChest extends CustomChest {

    private int keyAmount = 0;

    public KeyChest(UUID uuid, UUID owner) {
        super(uuid, owner, ChestType.KEY_CHEST);
    }

    @Override
    public void open(Player player) {
        // TODO: Open the chest.
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

    @Override
    public void animation() {
        ArmorStand armorStand = KatsuUtils.chestArmorStand(getLocation(), getUUID());
        if (armorStand == null) return;

        new KatsuTask(KatsuChest.getInstance(), 2) {
            private int i = 0;

            @Override
            public void run() {
                if (i % 2 == 0) {
                    armorStand.setHeadPose(new EulerAngle(0, 0, Math.toRadians(10f)));

                } else {
                    armorStand.setHeadPose(new EulerAngle(0, 0, Math.toRadians(350f)));
                }

                if (getCurrentCount() == 0) {
                    armorStand.setHeadPose(new EulerAngle(0, 0, 0));
                }
                i++;
            }
        }.countDownTask(4);
    }

    public int getKeyAmount() {
        return keyAmount;
    }

    public void setKeyAmount(int keyAmount) {
        this.keyAmount = keyAmount;
    }
}
