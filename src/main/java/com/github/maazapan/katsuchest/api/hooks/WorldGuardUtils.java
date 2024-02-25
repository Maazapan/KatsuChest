package com.github.maazapan.katsuchest.api.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardUtils {

    public static boolean hasWorldGuard() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    public static boolean canPlace(Player player, Location location) {
        WorldGuardPlugin plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        LocalPlayer localPlayer = plugin.wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()))) {
            return true;
        }
        return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_PLACE);
    }

    public static boolean canBreak(Player player, Location location) {
        WorldGuardPlugin plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        LocalPlayer localPlayer = plugin.wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()))) {
            return true;
        }
        return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK);
    }

    public static boolean canInteract(Player player, Location location) {
        WorldGuardPlugin plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        LocalPlayer localPlayer = plugin.wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()))) {
            return true;
        }
        return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.INTERACT);
    }
}
