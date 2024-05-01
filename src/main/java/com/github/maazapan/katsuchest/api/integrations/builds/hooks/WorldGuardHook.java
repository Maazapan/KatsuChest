package com.github.maazapan.katsuchest.api.integrations.builds.hooks;

import com.github.maazapan.katsuchest.api.integrations.builds.BuildHook;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook implements BuildHook {
    @Override
    public String getName() {
        return "WorldGuard";
    }

    @Override
    public boolean canUse(Player player, Location location) {
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionQuery query = container.createQuery();
        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()))) {
            return true;
        }
        return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BUILD);
    }
}
