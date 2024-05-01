package com.github.maazapan.katsuchest.api.integrations.builds.hooks;

import com.github.maazapan.katsuchest.api.integrations.builds.BuildHook;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyAdvancedHook implements BuildHook {
    @Override
    public String getName() {
        return "Towny";
    }

    @Override
    public boolean canUse(Player player, Location location) {
        final TownBlock block = TownyAPI.getInstance().getTownBlock(location);
        if(block != null) return PlayerCacheUtil.getCachePermission(player, location, location.getBlock().getType(), TownyPermission.ActionType.BUILD);
        return true;
    }
}
