package com.github.maazapan.katsuchest.api.integrations.builds.hooks;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.api.integrations.builds.BuildHook;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LandsHook implements BuildHook {

    @Override
    public String getName() {
        return "Lands";
    }

    @Override
    public boolean canUse(Player player, Location location) {
        LandsIntegration api = LandsIntegration.of(KatsuChest.getInstance());
        Area area = api.getArea(location);

        if (area == null) return true;
        return area.hasFlag(player, Flags.BLOCK_PLACE, false);

    }
}
