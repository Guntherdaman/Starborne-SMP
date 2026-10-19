
package com.starborne.rituals;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;

public final class LegendaryItemFactory {

    private final NamespacedKey legendaryIdKey;

    public LegendaryItemFactory(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException(
                    "Plugin cannot be null."
            );
        }

        this.legendaryIdKey = new NamespacedKey(
                plugin,
                "legendary_item_id"
        );
    }

    public ItemStack create(LegendaryRecipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException(
                    "Recipe cannot be null."
            );
        }

        ItemStack item = new ItemStack(recipe.getIcon());
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException(
                    "Could not create metadata for recipe: "
                            + recipe.getId()
            );
        }

        meta.setDisplayName(
                ChatColor.LIGHT_PURPLE + recipe.getDisplayName()
        );

        meta.setLore(List.of(
                ChatColor.GRAY + "A Starborne Legendary.",
                ChatColor.DARK_PURPLE + "Bound to its unique identity."
        ));

        meta.getPersistentDataContainer().set(
                legendaryIdKey,
                PersistentDataType.STRING,
                recipe.getId().toLowerCase(Locale.ROOT)
        );

        item.setItemMeta(meta);
        return item;
    }

    public String getLegendaryId(ItemStack item) {
        if (item == null || item.getType().isAir()
                || !item.hasItemMeta()) {
            return null;
        }

        return item.getItemMeta()
                .getPersistentDataContainer()
                .get(legendaryIdKey, PersistentDataType.STRING);
    }

    public boolean isLegendary(ItemStack item) {
        return getLegendaryId(item) != null;
    }
}
