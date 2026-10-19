
package com.starborne.rituals;

import org.bukkit.ChatColor;
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
import java.util.Locale;

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
                plugin, "legendary_item_id"
        );

        customDamageKey = new NamespacedKey(
                plugin, "legendary_custom_damage"
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

        if (legendaryId == null
                || item.getType().getMaxDurability() <= 0) {
            return;
        }

        // Prevent Minecraft's native durability from being applied.
        event.setCancelled(true);

        int currentDamage = meta.getPersistentDataContainer().getOrDefault(
                customDamageKey,
                PersistentDataType.INTEGER,
                0
        );

        int damageToApply = Math.max(1, event.getDamage());
        int newDamage = Math.min(
                MAX_DURABILITY,
                currentDamage + damageToApply
        );

        if (newDamage >= MAX_DURABILITY) {
            // Schedule removal after the current damage event.
            Player player = event.getPlayer();
            ItemStack damagedItem = item;

            player.getScheduler().run(
                    JavaPlugin.getProvidingPlugin(getClass()),
                    task -> {
                        if (damagedItem.getAmount() > 0) {
                            damagedItem.setAmount(0);
                        }

                        player.sendMessage(
                                ChatColor.RED + "Your Legendary "
                                        + readableName(legendaryId)
                                        + " has broken!"
                        );
                    },
                    null
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

        lore.removeIf(line -> {
            String plain = ChatColor.stripColor(line);
            return plain != null
                    && plain.startsWith("Durability: ");
        });

        lore.add(
                ChatColor.GRAY + "Durability: "
                        + ChatColor.GREEN
                        + (MAX_DURABILITY - damage)
                        + ChatColor.GRAY + "/" + MAX_DURABILITY
        );

        meta.setLore(lore);
    }

    private String readableName(String id) {
        String[] words = id.toLowerCase(Locale.ROOT).split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (result.length() > 0) {
                result.append(' ');
            }

            result.append(
                    Character.toUpperCase(word.charAt(0))
            ).append(word.substring(1));
        }

        return result.toString();
    }
}
