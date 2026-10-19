
package com.starborne.rituals;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class LegendaryItemFactory {

    private static final int MAX_DURABILITY = 600;

    private final NamespacedKey legendaryIdKey;
    private final NamespacedKey customDamageKey;

    private static final Map<String, Material> ITEM_MATERIALS =
            Map.ofEntries(
                    Map.entry("resistance_chestplate",
                            Material.NETHERITE_CHESTPLATE),
                    Map.entry("health_chestplate",
                            Material.NETHERITE_CHESTPLATE),
                    Map.entry("health_leggings",
                            Material.NETHERITE_LEGGINGS),
                    Map.entry("health_helmet",
                            Material.NETHERITE_HELMET),
                    Map.entry("health_boots",
                            Material.NETHERITE_BOOTS),
                    Map.entry("axe_of_greed",
                            Material.NETHERITE_AXE),
                    Map.entry("villager_talisman",
                            Material.TOTEM_OF_UNDYING),
                    Map.entry("cactus_leggings",
                            Material.NETHERITE_LEGGINGS),
                    Map.entry("breezy_boots",
                            Material.NETHERITE_BOOTS),
                    Map.entry("thousand_arrow_crossbow",
                            Material.CROSSBOW),
                    Map.entry("warden_helmet",
                            Material.NETHERITE_HELMET),
                    Map.entry("warden_battleaxe",
                            Material.NETHERITE_AXE),
                    Map.entry("torrential_trident",
                            Material.TRIDENT),
                    Map.entry("ice_talisman",
                            Material.PRISMARINE_SHARD),
                    Map.entry("eternal_golden_apple",
                            Material.GOLDEN_APPLE),
                    Map.entry("fruitful_spear",
                            Material.TRIDENT),
                    Map.entry("gravity_bow",
                            Material.BOW),
                    Map.entry("regeneration_bucket",
                            Material.MILK_BUCKET),
                    Map.entry("poison_sword",
                            Material.NETHERITE_SWORD)
            );

    public LegendaryItemFactory(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException(
                    "Plugin cannot be null."
            );
        }

        legendaryIdKey = new NamespacedKey(
                plugin, "legendary_item_id"
        );

        customDamageKey = new NamespacedKey(
                plugin, "legendary_custom_damage"
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
                    "No reward material registered for recipe ID: "
                            + id
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

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "A Starborne Legendary.");
        lore.add(
                ChatColor.DARK_PURPLE
                        + "Bound to its unique identity."
        );

        meta.getPersistentDataContainer().set(
                legendaryIdKey,
                PersistentDataType.STRING,
                id
        );

        // Initialize custom durability only for damageable items.
        if (material.getMaxDurability() > 0) {
            meta.getPersistentDataContainer().set(
                    customDamageKey,
                    PersistentDataType.INTEGER,
                    0
            );

            lore.add(
                    ChatColor.GRAY + "Durability: "
                            + ChatColor.GREEN + MAX_DURABILITY
                            + ChatColor.GRAY + "/" + MAX_DURABILITY
            );
        }

        meta.setLore(lore);
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
                .get(
                        legendaryIdKey,
                        PersistentDataType.STRING
                );
    }

    public boolean isLegendary(ItemStack item) {
        return getLegendaryId(item) != null;
    }

    public int getCustomDamage(ItemStack item) {
        if (!isLegendary(item)) {
            return 0;
        }

        return item.getItemMeta()
                .getPersistentDataContainer()
                .getOrDefault(
                        customDamageKey,
                        PersistentDataType.INTEGER,
                        0
                );
    }

    public int getRemainingDurability(ItemStack item) {
        if (!isLegendary(item)
                || item.getType().getMaxDurability() <= 0) {
            return 0;
        }

        return Math.max(
                0,
                MAX_DURABILITY - getCustomDamage(item)
        );
    }
}
