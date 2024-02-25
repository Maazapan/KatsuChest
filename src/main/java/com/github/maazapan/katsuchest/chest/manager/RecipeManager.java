package com.github.maazapan.katsuchest.chest.manager;

import com.github.maazapan.katsuchest.KatsuChest;
import com.github.maazapan.katsuchest.chest.enums.ChestType;
import com.github.maazapan.katsuchest.utils.file.FileManager;
import com.github.maazapan.katsuchest.utils.file.enums.FileType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeManager {

    private final KatsuChest plugin;

    public RecipeManager(KatsuChest plugin) {
        this.plugin = plugin;
    }

    public void registerRecipes() {
        FileManager config = new FileManager(plugin, FileType.CONFIG);
        ChestManager chestManager = plugin.getChestManager();

        try {
            for (ChestType chestType : ChestType.values()) {
                if (!config.getBoolean("config.custom-chest." + chestType + ".craft-recipe.enable")) continue;
                ItemStack itemStack = chestManager.getCustomChestItem(chestType);

                NamespacedKey key = new NamespacedKey(plugin, chestType.toString());
                ShapedRecipe recipe = new ShapedRecipe(key, itemStack);

                List<String> pattern = config.getStringList("config.custom-chest." + chestType + ".craft-recipe.pattern")
                        .stream().map(s -> s.replaceAll(" ", "")).collect(Collectors.toList());

                recipe.shape(pattern.get(0), pattern.get(1), pattern.get(2));

                for (String s : config.getSection("config.custom-chest." + chestType + ".craft-recipe.ingredients").getKeys(false)) {
                    String material = config.get("config.custom-chest." + chestType + ".craft-recipe.ingredients." + s);
                    recipe.setIngredient(s.charAt(0), Material.valueOf(material));
                }
                Bukkit.addRecipe(recipe);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Han error occurred on register custom recipe.");
            e.printStackTrace();
        }
    }
}
