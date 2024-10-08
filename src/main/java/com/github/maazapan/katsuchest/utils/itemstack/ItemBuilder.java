package com.github.maazapan.katsuchest.utils.itemstack;

import com.github.maazapan.katsuchest.utils.KatsuUtils;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack is;
    private String path;
    private FileConfiguration config;

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m The material to create the ItemBuilder with.
     */
    public ItemBuilder(Material m) {
        this(m, 1);
    }

    public ItemBuilder(){

    }

    /**
     * Create a new ItemBuilder over an existing itemstack.
     *
     * @param is The itemstack to create the ItemBuilder over.
     */
    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m      The material of the item.
     * @param amount The amount of the item.
     */
    public ItemBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m          The material of the item.
     * @param amount     The amount of the item.
     * @param durability The durability of the item.
     */
    public ItemBuilder(Material m, int amount, byte durability) {
        is = new ItemStack(m, amount, durability);
    }

    /**
     * Clone the ItemBuilder into a new one.
     *
     * @return The cloned instance.
     */
    public ItemBuilder clone() {
        return new ItemBuilder(is);
    }

    /**
     * Change the durability of the item.
     *
     * @param dur The durability to set it to.
     */
    public ItemBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    /**
     * Set the displayname of the item.
     *
     * @param name The name to change it to.
     */
    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(KatsuUtils.coloredHex(name));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add an unsafe enchantment.
     *
     * @param ench  The enchantment to add.
     * @param level The level to put the enchant on.
     */
    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        is.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder setAmount(int amount){
        is.setAmount(amount);
        return this;
    }

    /**
     * Remove a certain enchant from the item.
     *
     * @param ench The enchantment to remove
     */
    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    /**
     * Set the skull owner for the item. Works on skulls only.
     *
     * @param owner The name of the skull's owner.
     */
    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }


    public ItemBuilder setSkullOwner(UUID owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(Bukkit.getOfflinePlayer(owner).getName());
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }


    public ItemBuilder setNBT(String key, String value){
        NBT.modify(is, nbt ->
        {
            nbt.setString(key, value);
        });
        return this;
    }

    public ItemBuilder setNBT(String key, UUID value){
        NBT.modify(is, nbt ->
        {
            nbt.setUUID(key, value);
        });
        return this;
    }

    public ItemBuilder setSkullBase64(String base64) {
        is = new ItemStack(Material.PLAYER_HEAD);
        PlayerProfile profile = Bukkit.getServer().createPlayerProfile(UUID.randomUUID(), "KatsuHead");

        try {
            profile.getTextures().setSkin(new URL(KatsuUtils.getURLFromBase64(base64)));
            SkullMeta meta = (SkullMeta) is.getItemMeta();
            meta.setOwnerProfile(profile);
            is.setItemMeta(meta);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ItemBuilder setMetaData(Plugin plugin, String custom_key) {
        NamespacedKey key = new NamespacedKey(plugin, custom_key);
        ItemMeta meta = is.getItemMeta();

        meta.getCustomTagContainer().setCustomTag(key, ItemTagType.DOUBLE, Math.PI);
        is.setItemMeta(meta);

        return this;
    }

    /**
     * Add an enchant to the item.
     *
     * @param ench  The enchant to add
     * @param level The level
     */
    public ItemBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = is.getItemMeta();
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add multiple enchants at once.
     *
     * @param enchantments The enchants to add.
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    /**
     * Sets infinity durability on the item by setting the durability to Short.MAX_VALUE.
     */
    public ItemBuilder setInfinityDurability() {
        is.setDurability(Short.MAX_VALUE);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param list The lore to set it to.
     */
    public ItemBuilder setLore(String... list) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();

        for (String s : list) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param a The lore to set it to.
     */
    public ItemBuilder setLore(List<String> a) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();

        for(String s : a){
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     *
     * @param
     */
    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) return this;
        lore.remove(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     *
     * @param index The index of the lore line to remove.
     */
    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index > lore.size()) return this;
        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     */
    public ItemBuilder addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore = new ArrayList<>(im.getLore());
        lore.add(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setModelData(Integer data){
        ItemMeta im = is.getItemMeta();
        im.setCustomModelData(data);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     * @param pos  The index of where to put it.
     */
    public ItemBuilder addLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
     *
     * @param color The color to set it to.
     */
    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
            im.setColor(color);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    public ItemBuilder fromConfig(FileConfiguration config, String path) {
        this.path = path;
        this.config = config;

        this.is = new ItemStack(Material.valueOf(config.getString(path + ".id")));

        if (config.contains(path + ".texture")) {
            this.setSkullBase64(config.getString(path + ".texture"));
        }

        if (config.contains(path + ".display_name")) {
            this.setName(config.getString(path + ".display_name"));
        }

        if (config.contains(path + ".lore")) {
            this.setLore(config.getStringList(path + ".lore"));
        }

        if (config.contains(path + ".enchantments")) {
            for (String enchant : config.getStringList(path + ".enchantments")) {
                String[] split = enchant.split(":");
                this.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]));
            }
        }

        if (config.contains(path + ".durability")) {
            this.setDurability((short) config.getInt(path + ".durability"));
        }

        if (config.contains(path + ".model_data")) {
            this.setModelData(config.getInt(path + ".model_data"));
        }

        if (config.contains(path + ".actions")) {
            NBT.modify(is, nbt ->
            {
                nbt.setByteArray("katsu-chest-action", KatsuUtils.toListBytes(config.getStringList(path + ".actions")));
            });
        }
        return this;
    }


    public ItemBuilder replace(String replace, String replacement) {
        ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(KatsuUtils.coloredHex(meta.getDisplayName().replaceAll(replace, replacement)));

        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            lore.replaceAll(s -> KatsuUtils.coloredHex(s.replaceAll(replace, replacement)));
            meta.setLore(lore);
        }
        is.setItemMeta(meta);
        return this;
    }

    public int getSlot() {
        return config.getInt(path + ".slot");
    }


    /**
     * Retrieves the itemstack from the ItemBuilder.
     *
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    public ItemStack toItemStack() {
        return is;
    }
}
