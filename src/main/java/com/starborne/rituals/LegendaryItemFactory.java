
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
import java.util.Map;

public final class LegendaryItemFactory {

    private final NamespacedKey legendaryIdKey;

    /*
     * These IDs must match the IDs registered in FirstWaveRecipes.
     * Add or adjust entries to match your exact recipe IDs.
     */
    private static final Map<String, Material> ITEM_MATERIALS = Map.ofEntries(
            Map.entry("resistance_chestplate", Material.NETHERITE_CHESTPLATE),
            Map.entry("health_chestplate", Material.NETHERITE_CHESTPLATE),
            Map.entry("health_leggings", Material.NETHERITE_LEGGINGS),
            Map.entry("health_helmet", Material.NETHERITE_HELMET),
            Map.entry("health_boots", Material.NETHERITE_BOOTS),
            Map.entry("axe_of_greed", Material.NETHERITE_AXE),
            Map.entry("villager_talisman", Material.TOTEM_OF_UNDYING),
            Map.entry("cactus_leggings", Material.NETHERITE_LEGGINGS),
            Map.entry("breezy_boots", Material.NETHERITE_BOOTS),
            Map.entry("thousand_arrow_crossbow", Material.CROSSBOW),
            Map.entry("warden_helmet", Material.NETHERITE_HELMET),
            Map.entry("warden_battleaxe", Material.NETHERITE_AXE),
            Map.entry("torrential_trident", Material.TRIDENT),
            Map.entry("ice_talisman", Material.PRISMARINE_SHARD),
            Map.entry("eternal_golden_apple", Material.GOLDEN_APPLE),
            Map.entry("fruitful_spear", Material.TRIDENT),
            Map.entry("gravity_bow", Material.BOW),
            Map.entry("regeneration_bucket", Material.MILK_BUCKET),
            Map.entry("poison_sword", Material.NETHERITE_SWORD)
    );

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

        String id = recipe.getId().toLowerCase(Locale.ROOT);
        Material material = ITEM_MATERIALS.get(id);

        if (material == null) {
            throw new IllegalArgumentException(
                    "No reward material registered for recipe ID: " + id
            );
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException(
                    "Could not create metadata for recipe: " + id
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
                id
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
