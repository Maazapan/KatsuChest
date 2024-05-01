package com.github.maazapan.katsuchest.api.integrations.builds.hooks;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import com.github.maazapan.katsuchest.api.integrations.builds.BuildHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RedProtectHook implements BuildHook {

    @Override
    public String getName() {
        return "RedProtect";
    }

    @Override
    public boolean canUse(Player player, Location location) {
        final Region region = RedProtect.get().getAPI().getRegion(location);

        if(region != null) return (region.canBuild(player) && region.canBreak(location.getBlock().getType()));
        return true;
    }
}
