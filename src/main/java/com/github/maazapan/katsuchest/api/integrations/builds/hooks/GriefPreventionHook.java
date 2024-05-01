package com.github.maazapan.katsuchest.api.integrations.builds.hooks;

import com.github.maazapan.katsuchest.api.integrations.builds.BuildHook;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionHook implements BuildHook {

    @Override
    public String getName() {
        return "GriefPrevention";
    }

    @Override
    public boolean canUse(Player player, Location location) {
        final Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, false, null);
        if(claim != null) return claim.hasExplicitPermission(player, ClaimPermission.Build);
        return true;
    }
}
