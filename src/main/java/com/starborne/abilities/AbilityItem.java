
package com.starborne.abilities;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class AbilityItem {

    private final NamespacedKey abilityKey;

    public AbilityItem(JavaPlugin plugin) {
        this.abilityKey = new NamespacedKey(
                plugin,
                "ability_id"
        );
    }

    public void setAbilityId(
            ItemStack item,
            String abilityId
    ) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.getPersistentDataContainer().set(
                abilityKey,
                PersistentDataType.STRING,
                abilityId.toLowerCase()
        );

        item.setItemMeta(meta);
    }

    public String getAbilityId(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(
                abilityKey,
                PersistentDataType.STRING
        );
    }
}

