package com.github.maazapan.katsuchest.commands;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.gui.PanelGUI;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChestCommand implements CommandExecutor, TabCompleter {

    private final KatsuChest plugin;

    public ChestCommand(KatsuChest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("Only players can use this command!");
            return true;
        }

        FileManager config = new FileManager(plugin, FileType.MESSAGES);
        Player player = (Player) sender;

        if (!(args.length > 0)) {
            player.sendMessage(config.getWithPrefix("no-args"));
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload": {
                plugin.reloadConfig();
                plugin.saveDefaultConfig();

                player.sendMessage(KatsuUtils.coloredHex("&aReloaded config.yml"));
            }
            break;


            case "test": {
                new PanelGUI(player, plugin).init();
            }
            break;

            /*
             + Get an item chest at player.
             - Command: /katsuchest give <id_chest>
             */
            case "give": {
                if (!player.hasPermission("katsuchest.command.give")) {
                    player.sendMessage(config.getWithPrefix("no-permission"));
                    return true;
                }

                if (!(args.length > 1)) {
                    player.sendMessage(config.getWithPrefix("no-args-give"));
                    return true;
                }

                ChestType chestType = ChestType.getByName(args[1]);

                if (chestType == null) {
                    player.sendMessage(config.getWithPrefix("no-exist-chest"));
                    return true;
                }

                ChestManager chestManager = plugin.getChestManager();
                ItemStack itemStack = chestManager.getCustomChestItem(chestType);

                player.getInventory().addItem(itemStack);
                player.sendMessage(config.getWithPrefix("give-chest")
                        .replace("%chest%", chestType.toString()));
            }
            break;


            default:
                player.sendMessage(config.getWithPrefix("no-args"));
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> subCommands = Arrays.asList("give", "reload", "help");
        List<String> chestTypes = Arrays.stream(ChestType.values())
                .map(Objects::toString)
                .collect(Collectors.toList());

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("give")) {
                return chestTypes;
            }
            if (args.length == 1) return subCommands;
        }
        return null;
    }
}
