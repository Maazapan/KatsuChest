package com.github.maazapan.katsuchest.api.integrations;

import com.github.maazapan.katsuchest.api.integrations.builds.BuildHook;
import com.github.maazapan.katsuchest.api.integrations.builds.hooks.*;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class IntegrationManager {

    private final List<BuildHook> buildIntegrations = Lists.newArrayList(
            new WorldGuardHook(),
            new RedProtectHook(),
            new GriefPreventionHook(),
            new TownyAdvancedHook(),
            new LandsHook());

    /**
     * Load all integrations and check if they are enabled.
     */
    public void load() {
        // Check if the integration is enabled.
        buildIntegrations.removeIf(buildHook -> Bukkit.getPluginManager().getPlugin(buildHook.getName()) == null);
    }

    public boolean canUse(Player player, Location location) {
        if (buildIntegrations.isEmpty()) return true;
        for (BuildHook buildHook : buildIntegrations) {
            return buildHook.canUse(player, location);
        }
        return false;
    }
}
