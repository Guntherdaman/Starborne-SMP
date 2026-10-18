
package com.starborne.souls;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SoulStarItem {

    private final NamespacedKey tierKey;
    private final NamespacedKey powerKey;

    public SoulStarItem(JavaPlugin plugin) {
        tierKey = new NamespacedKey(plugin, "soul_tier");
        powerKey = new NamespacedKey(plugin, "soul_power");
    }

    public ItemStack create(PvPTier tier) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§d§l" + tier.name() + "-Tier Soul Star");
        meta.setLore(List.of(
                "§7A fragment of a player's soul.",
                "§7Tier: §d" + tier.name(),
                "§7Soul Power: §d" + tier.getSoulPower(),
                "§8Used to empower special equipment."
        ));

        meta.getPersistentDataContainer().set(
                tierKey,
                PersistentDataType.STRING,
                tier.name()
        );

        meta.getPersistentDataContainer().set(
                powerKey,
                PersistentDataType.INTEGER,
                tier.getSoulPower()
        );

        item.setItemMeta(meta);
        return item;
    }

    public boolean isSoulStar(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR
                || !item.hasItemMeta()) {
            return false;
        }

        return item.getItemMeta()
                .getPersistentDataContainer()
                .has(tierKey, PersistentDataType.STRING);
    }

    public PvPTier getTier(ItemStack item) {
        if (!isSoulStar(item)) {
            return null;
        }

        String tierName = item.getItemMeta()
                .getPersistentDataContainer()
                .get(tierKey, PersistentDataType.STRING);

        try {
            return PvPTier.valueOf(tierName);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return null;
        }
    }

    public int getSoulPower(ItemStack item) {
        if (!isSoulStar(item)) {
            return 0;
        }

        Integer power = item.getItemMeta()
                .getPersistentDataContainer()
                .get(powerKey, PersistentDataType.INTEGER);

        return power == null ? 0 : power;
    }
}
