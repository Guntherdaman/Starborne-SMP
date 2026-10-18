
package com.starborne.souls;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class SoulPowerManager {

    public static final int LEGENDARY_MAX_SOUL = 200;
    public static final int MYTHIC_MAX_SOUL = 400;

    private final NamespacedKey itemTypeKey;
    private final NamespacedKey soulPowerKey;

    public SoulPowerManager(JavaPlugin plugin) {
        itemTypeKey = new NamespacedKey(plugin, "soul_item_type");
        soulPowerKey = new NamespacedKey(plugin, "item_soul_power");
    }

    public void markAsLegendary(ItemStack item) {
        markItem(item, "LEGENDARY");
    }

    public void markAsMythic(ItemStack item) {
        markItem(item, "MYTHIC");
    }

    private void markItem(ItemStack item, String type) {
        if (item == null || item.getType().isAir()
                || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(
                itemTypeKey,
                PersistentDataType.STRING,
                type
        );

        meta.getPersistentDataContainer().set(
                soulPowerKey,
                PersistentDataType.INTEGER,
                0
        );

        item.setItemMeta(meta);
    }

    public boolean isPowerItem(ItemStack item) {
        if (item == null || item.getType().isAir()
                || !item.hasItemMeta()) {
            return false;
        }

        String type = item.getItemMeta()
                .getPersistentDataContainer()
                .get(itemTypeKey, PersistentDataType.STRING);

        return "LEGENDARY".equals(type) || "MYTHIC".equals(type);
    }

    public String getItemType(ItemStack item) {
        if (!isPowerItem(item)) {
            return null;
        }

        return item.getItemMeta()
                .getPersistentDataContainer()
                .get(itemTypeKey, PersistentDataType.STRING);
    }

    public int getSoulPower(ItemStack item) {
        if (!isPowerItem(item)) {
            return 0;
        }

        Integer power = item.getItemMeta()
                .getPersistentDataContainer()
                .get(soulPowerKey, PersistentDataType.INTEGER);

        return power == null ? 0 : power;
    }

    public int getMaximumSoulPower(ItemStack item) {
        String type = getItemType(item);

        if ("LEGENDARY".equals(type)) {
            return LEGENDARY_MAX_SOUL;
        }

        if ("MYTHIC".equals(type)) {
            return MYTHIC_MAX_SOUL;
        }

        return 0;
    }

    public boolean canAcceptSoulStar(
            ItemStack target,
            ItemStack soulStar,
            SoulStarItem soulStarItem
    ) {
        if (!isPowerItem(target)
                || !soulStarItem.isSoulStar(soulStar)) {
            return false;
        }

        int current = getSoulPower(target);
        int added = soulStarItem.getSoulPower(soulStar);
        int maximum = getMaximumSoulPower(target);

        return added > 0
                && current <= maximum
                && added <= maximum - current;
    }

    public boolean applySoulStar(
            ItemStack target,
            ItemStack soulStar,
            SoulStarItem soulStarItem
    ) {
        if (!canAcceptSoulStar(target, soulStar, soulStarItem)) {
            return false;
        }

        int current = getSoulPower(target);
        int added = soulStarItem.getSoulPower(soulStar);

        ItemMeta meta = target.getItemMeta();

        meta.getPersistentDataContainer().set(
                soulPowerKey,
                PersistentDataType.INTEGER,
                current + added
        );

        target.setItemMeta(meta);

        // The caller should consume one Soul Star only after success.
        return true;
    }
}

