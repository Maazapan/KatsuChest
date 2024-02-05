package com.github.maazapan.katsuchest.chest.gui;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.api.ChestWrongPinEvent;
import com.github.maazapan.katsuchest.chest.types.PanelChest;
import com.github.maazapan.katsuchest.utils.KatsuUtils;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import com.github.maazapan.katsuchest.utils.gui.InventoryGUI;
import com.github.maazapan.katsuchest.utils.itemstack.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PanelGUI extends InventoryGUI {

    private final Player player;

    private final PanelChest panelChest;
    private final FileManager config;

    private final int MAX_DIGITS;
    private final Chest chest;

    private String password = "";
    private final KatsuChest plugin;

    public PanelGUI(Player player, KatsuChest plugin, PanelChest panelChest, Chest chest) {
        super(plugin, "config.inventory.panel-chest");
        this.config = new FileManager(plugin, FileType.CONFIG);
        this.MAX_DIGITS = config.getInt("config.max-pin-length");
        this.panelChest = panelChest;
        this.plugin = plugin;
        this.player = player;
        this.chest = chest;
    }

    @Override
    @SuppressWarnings("all")
    public void onClick(InventoryClickEvent event) {
        NBTItem nbtItem = new NBTItem(event.getCurrentItem());
        event.setCancelled(true);

        if (nbtItem.hasTag("katsu-chest-action")) {
            List<String> stringList = nbtItem.getObject("katsu-chest-action", List.class);
            FileManager messages = new FileManager(plugin, FileType.MESSAGES);

            for (String actions : stringList) {
                String[] action = actions.split(": ");

                switch (action[0]) {
                    // Check if the action is a number. If it is, check if the password length is less than the max digits.
                    case "[NUMBER]": {
                        String number = action[1];

                        if (!(password.length() < MAX_DIGITS)) {
                            KatsuUtils.parseSound(player, config.get("config.sound.max-digits"));
                            return;
                        }
                        KatsuUtils.parseSound(player, config.get("config.sound.add-digits"));
                        password += number;
                        this.init();
                    }
                    break;

                    // Confirm the password.
                    case "[CONFIRM]": {
                        if (panelChest.hasPassword() && !panelChest.getPassword().equalsIgnoreCase(password)) {
                            KatsuUtils.parseSound(player, config.get("config.sound.wrong-password"));
                            player.closeInventory();

                            ChestWrongPinEvent chestEvent = new ChestWrongPinEvent(panelChest, player);
                            Bukkit.getPluginManager().callEvent(chestEvent);

                            panelChest.animation();
                            return;
                        }

                        if (!panelChest.hasPassword()) {
                            player.sendMessage(messages.getWithPrefix("chest-set-pin").replaceAll("%pin%", password));
                        }

                        panelChest.hasPassword(true);
                        panelChest.setPassword(password);

                        player.openInventory(chest.getInventory());
                    }
                    break;

                    // Reset the password.
                    case "[RESET]": {
                        KatsuUtils.parseSound(player, config.get("config.sound.reset-digits"));
                        password = "";
                        this.init();
                    }
                    break;

                    // Cancel the action.
                    case "[CANCEL]": {
                        player.closeInventory();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void init() {
        this.createGUI();

        ItemStack itemStack = new ItemBuilder()
                .fromConfig(config.toConfig(), "config.inventory.panel-chest.digits-display")
                .replace("%pin%", formatPin());

        getInventory().setItem(15, itemStack);
        this.open(player);
    }


    private String formatPin() {
        StringBuilder pin = new StringBuilder();

        for (int i = 0; i < MAX_DIGITS; i++) {
            if (password.length() > i) {
                pin.append("&a&n").append(password.charAt(i)).append("&r&a ");
                continue;
            }
            pin.append("&a_ ");
        }
        return pin.toString();
    }

    public PanelChest getPanelChest() {
        return panelChest;
    }
}
