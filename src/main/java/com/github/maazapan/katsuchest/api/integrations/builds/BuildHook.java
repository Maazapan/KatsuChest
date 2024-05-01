package com.github.maazapan.katsuchest.api.integrations.builds;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BuildHook {

    String getName();

    boolean canUse(Player player, Location location);
}
