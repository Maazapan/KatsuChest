package com.github.maazapan.katsuchest.commands;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.CustomChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.chest.manager.ChestManager;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChestCommand implements CommandExecutor, TabCompleter {

    private final KatsuChest plugin;

    public ChestCommand(KatsuChest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        FileManager messages = new FileManager(plugin, FileType.MESSAGES);
        FileManager config = new FileManager(plugin, FileType.CONFIG);

        if (!(args.length > 0)) {
            sender.sendMessage(messages.getWithPrefix("no-args"));
            return true;
        }

        switch (args[0].toLowerCase()) {

            /*
             + Lock or unlock a custom chest.
             - Command: /katsuchest lock
             */
            case "lock": {
                if (!sender.hasPermission("katsuchest.command.lock")) {
                    sender.sendMessage(messages.getWithPrefix("no-permission"));
                    return true;
                }

                if (!(sender instanceof Player)) {
                    sender.sendMessage(messages.getWithPrefix("no-console"));
                    return true;
                }

                Player player = (Player) sender;

                ChestManager chestManager = plugin.getChestManager();
                Block block = player.getTargetBlock(null, 5);

                if (block.getType() != Material.CHEST || !chestManager.isCustomChest(block)) {
                    sender.sendMessage(messages.getWithPrefix("no-chest"));
                    return true;
                }

                UUID chestUUID = chestManager.getCustomChestUUID(block);
                CustomChest customChest = chestManager.getCustomChest(chestUUID);

                String message = customChest.isLocked() ? "unlock-chest" : "lock-chest";
                customChest.setLocked(!customChest.isLocked());

                sender.sendMessage(messages.getWithPrefix(message));
            }
            break;

            /*
             + Help command.
             - Command: /katsuchest help
             */
            case "help": {
                if (!sender.hasPermission("katsuchest.command.help")) {
                    sender.sendMessage(messages.getWithPrefix("no-permission"));
                    return true;
                }

                for (String string : messages.getStringList("help-list")) {
                    sender.sendMessage(KatsuUtils.coloredHex(string)
                            .replaceAll("%prefix%", plugin.getPrefix())
                            .replaceAll("%version%", plugin.getDescription().getVersion()));
                }
            }
            break;

            /*
             + Reload all plugin configs.
             - Command: /katsuchest reload
             */
            case "reload": {
                if (!sender.hasPermission("katsuchest.command.reload")) {
                    sender.sendMessage(messages.getWithPrefix("no-permission"));
                    return true;
                }

                plugin.reloadConfig();
                plugin.saveDefaultConfig();

                messages.reload();

                sender.sendMessage(messages.getWithPrefix("reload"));
            }
            break;

            /*
             + Get an item chest at player.
             - Command: /katsuchest give <id_chest>
             - Command: /katsuchest give <id_chest> <player>
             */
            case "give": {
                if (!sender.hasPermission("katsuchest.command.give")) {
                    sender.sendMessage(messages.getWithPrefix("no-permission"));
                    return true;
                }

                if (!(args.length > 1)) {
                    sender.sendMessage(messages.getWithPrefix("no-args-give"));
                    return true;
                }

                ChestType chestType = ChestType.getByName(args[1]);

                if (chestType == null) {
                    sender.sendMessage(messages.getWithPrefix("no-exist-chest"));
                    return true;
                }

                ChestManager chestManager = plugin.getChestManager();
                ItemStack itemStack = chestManager.getCustomChestItem(chestType);

                // If the player is specified, give the chest to the player.
                if (args.length > 2) {
                    if (Bukkit.getPlayer(args[2]) == null) {
                        sender.sendMessage(messages.getWithPrefix("no-exist-player"));
                        return true;
                    }

                    Player player = Bukkit.getPlayer(args[2]);
                    player.getInventory().addItem(itemStack);

                } else {
                    Player player = (Player) sender;
                    player.getInventory().addItem(itemStack);
                }
                sender.sendMessage(messages.getWithPrefix("give-chest").replace("%chest%", chestType.toString()));
            }
            break;


            default:
                sender.sendMessage(messages.getWithPrefix("no-args"));
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> subCommands = Arrays.asList("give", "reload", "help", "lock");
        List<String> chestTypes = Arrays.stream(ChestType.values())
                .map(Objects::toString)
                .collect(Collectors.toList());

        if (args.length > 0) {
            if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                return chestTypes;
            }
            if (args.length == 1) return subCommands;
        }
        return null;
    }
}
