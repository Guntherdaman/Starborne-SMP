
package com.starborne.rituals;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class LegendaryDurabilityListener implements Listener {

    private static final int MAX_DURABILITY = 600;

    private final NamespacedKey legendaryIdKey;
    private final NamespacedKey customDamageKey;

    public LegendaryDurabilityListener(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException(
                    "Plugin cannot be null."
            );
        }

        legendaryIdKey = new NamespacedKey(
                plugin,
                "legendary_item_id"
        );

        customDamageKey = new NamespacedKey(
                plugin,
                "legendary_custom_damage"
        );
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();

        if (item == null || item.getType().isAir()
                || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        String legendaryId = meta.getPersistentDataContainer().get(
                legendaryIdKey,
                PersistentDataType.STRING
        );

        if (legendaryId == null) {
            return;
        }

        // Items such as talismans and apples do not have
        // normal durability, so do not apply this system to them.
        if (item.getType().getMaxDurability() <= 0) {
            return;
        }

        event.setCancelled(true);

        int currentDamage = meta.getPersistentDataContainer().getOrDefault(
                customDamageKey,
                PersistentDataType.INTEGER,
                0
        );

        int newDamage = Math.min(
                MAX_DURABILITY,
                currentDamage + Math.max(1, event.getDamage())
        );

        if (newDamage >= MAX_DURABILITY) {
            Player player = event.getPlayer();

            // Remove the broken item from the slot being damaged.
            item.setAmount(0);

            player.sendMessage(
                    ChatColor.RED + "Your Legendary "
                            + itemName(legendaryId)
                            + " has broken!"
            );
            return;
        }

        meta.getPersistentDataContainer().set(
                customDamageKey,
                PersistentDataType.INTEGER,
                newDamage
        );

        updateDurabilityLore(meta, newDamage);
        item.setItemMeta(meta);
    }

    private void updateDurabilityLore(
            ItemMeta meta,
            int damage
    ) {
        List<String> lore = meta.hasLore()
                ? new ArrayList<>(meta.getLore())
                : new ArrayList<>();

        lore.removeIf(line ->
                ChatColor.stripColor(line)
                        .startsWith("Durability: ")
        );

        lore.add(
                ChatColor.GRAY + "Durability: "
                        + ChatColor.GREEN
                        + (MAX_DURABILITY - damage)
                        + ChatColor.GRAY + "/" + MAX_DURABILITY
        );

        meta.setLore(lore);
    }

    private String itemName(String id) {
        return id.replace('_', ' ');
    }
}
