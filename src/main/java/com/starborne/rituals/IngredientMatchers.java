
package com.starborne.rituals;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.function.Predicate;

public final class IngredientMatchers {

    private IngredientMatchers() {
    }

    /**
     * Matches a potion with the requested base potion type.
     * Custom effects are rejected so modified potions cannot
     * masquerade as the required vanilla potion.
     */
    public static Predicate<ItemStack> potion(PotionType expected) {
        return item -> {
            if (item == null || expected == null) {
                return false;
            }

            if (item.getType() != Material.POTION
                    && item.getType() != Material.SPLASH_POTION
                    && item.getType() != Material.LINGERING_POTION) {
                return false;
            }

            ItemMeta meta = item.getItemMeta();

            if (!(meta instanceof PotionMeta potionMeta)) {
                return false;
            }

            return potionMeta.hasBasePotionType()
                    && potionMeta.getBasePotionType() == expected
                    && !potionMeta.hasCustomEffects();
        };
    }

    /**
     * Matches an enchanted book containing the exact requested
     * stored enchantment level.
     */
    public static Predicate<ItemStack> enchantedBook(
            Enchantment enchantment,
            int requiredLevel
    ) {
        return item -> {
            if (item == null
                    || item.getType() != Material.ENCHANTED_BOOK
                    || enchantment == null
                    || requiredLevel <= 0) {
                return false;
            }

            ItemMeta meta = item.getItemMeta();

            if (!(meta instanceof EnchantmentStorageMeta bookMeta)) {
                return false;
            }

            return bookMeta.getStoredEnchantLevel(enchantment)
                    == requiredLevel;
        };
    }

    /**
     * Matches an item that has at least the requested level
     * of each listed enchantment.
     */
    public static Predicate<ItemStack> enchantedItem(
            Enchantment first,
            int firstLevel,
            Enchantment second,
            int secondLevel
    ) {
        return item -> item != null
                && first != null
                && second != null
                && firstLevel > 0
                && secondLevel > 0
                && item.getItemMeta() != null
                && item.getEnchantmentLevel(first) >= firstLevel
                && item.getEnchantmentLevel(second) >= secondLevel;
    }

    /**
     * Matches a firework rocket with the requested flight duration.
     */
    public static Predicate<ItemStack> fireworkPower(int expectedPower) {
        return item -> {
            if (item == null
                    || item.getType() != Material.FIREWORK_ROCKET
                    || expectedPower < 1
                    || expectedPower > 3) {
                return false;
            }

            ItemMeta meta = item.getItemMeta();

            if (!(meta instanceof FireworkMeta fireworkMeta)) {
                return false;
            }

            return fireworkMeta.getPower() == expectedPower;
        };
    }
}
