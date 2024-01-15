package com.github.maazapan.katsuchest.utils;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KatsuUtils {

    public static String coloredHex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }
            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void parseSound(Player player, String sound) {
        String[] soundSplit = sound.split(";");

        player.playSound(player.getLocation(),
                Sound.valueOf(soundSplit[0]),
                Float.parseFloat(soundSplit[1]),
                Float.parseFloat(soundSplit[2]));
    }

    public static ArmorStand chestArmorStand(Location location, UUID chestUUID) {
        for (Entity entity : location.getWorld()
                .getNearbyEntities(location, 2, 2, 3)) {

            NBTEntity nbtEntity = new NBTEntity(entity);

            if (nbtEntity.getPersistentDataContainer().hasTag("katsu_chest_uuid")) {
                UUID uuid = nbtEntity.getPersistentDataContainer().getUUID("katsu_chest_uuid");

                if (chestUUID.equals(uuid)) return (ArmorStand) entity;
            }
        }
        return null;
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    public static Location stringToLocation(String string) {
        String[] split = string.split(",");
        return new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3])
        );
    }


    public static int getYawBlockFace(BlockFace facing) {
        switch (facing) {
            case NORTH:
                return 180;
            case SOUTH:
                return 0;
            case EAST:
                return -90;
            case WEST:
                return 90;
        }
        return 0;
    }

    public static String getURLFromBase64(String base64) {
        return new String(Base64.getDecoder().decode(base64.getBytes())).replace("{\"textures\":{\"SKIN\":{\"url\":\"", "").replace("\"}}}", "");
    }

    public static Location center(Location location) {
        return location.add(0.5, 0, 0.5);
    }
}
