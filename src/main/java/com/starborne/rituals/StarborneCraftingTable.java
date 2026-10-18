
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class StarborneCraftingTable {

    private final Starborne plugin;
    private final NamespacedKey itemKey;
    private final NamespacedKey recipeKey;

    public StarborneCraftingTable(Starborne plugin) {
        this.plugin = plugin;
        this.itemKey = new NamespacedKey(plugin, "starborne_crafting_table");
        this.recipeKey = new NamespacedKey(plugin, "starborne_table_recipe");
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(
                    ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD
                            + "Starborne Crafting Table"
            );

            meta.setLore(List.of(
                    ChatColor.GRAY + "A mystical crafting station.",
                    ChatColor.DARK_PURPLE + "Used to begin Legendary rituals."
            ));

            meta.getPersistentDataContainer().set(
                    itemKey,
                    PersistentDataType.BYTE,
                    (byte) 1
            );

            item.setItemMeta(meta);
        }

        return item;
    }

    public boolean isCustomTable(ItemStack item) {
        if (item == null || item.getType() != Material.CRAFTING_TABLE
                || !item.hasItemMeta()) {
            return false;
        }

        Byte marker = item.getItemMeta()
                .getPersistentDataContainer()
                .get(itemKey, PersistentDataType.BYTE);

        return marker != null && marker == (byte) 1;
    }

    public void registerRecipe() {
        ItemStack result = createItem();

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe.shape(
                "IDI",
                "DCD",
                "IDI"
        );

        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);

        // Avoid registering the same recipe twice.
        if (BukkitRecipeExists()) {
            plugin.getServer().removeRecipe(recipeKey);
        }

        plugin.getServer().addRecipe(recipe);
    }

    private boolean BukkitRecipeExists() {
        return plugin.getServer().getRecipe(recipeKey) != null;
    }
}

