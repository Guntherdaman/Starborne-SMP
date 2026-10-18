
package com.starborne.rituals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LegendaryIngredientChecker {

    public boolean hasIngredients(
            Player player,
            LegendaryRecipe recipe
    ) {
        return createAllocation(player, recipe) != null;
    }

    /**
     * Compatibility method for existing menus.
     * Returns missing amounts for all requirements using this material.
     */
    public int getMissingAmount(
            Player player,
            LegendaryRecipe recipe,
            Material material
    ) {
        if (player == null || recipe == null || material == null) {
            return 0;
        }

        int missing = 0;

        for (IngredientRequirement requirement : recipe.getRequirements()) {
            if (requirement.getMaterial() == material) {
                missing += getMissingAmount(
                        player, recipe, requirement
                );
            }
        }

        return missing;
    }

    /**
     * Returns the missing amount for one specific requirement.
     */
    public int getMissingAmount(
            Player player,
            LegendaryRecipe recipe,
            IngredientRequirement target
    ) {
        if (player == null || recipe == null || target == null) {
            return 0;
        }

        ItemStack[] contents =
                player.getInventory().getStorageContents();

        int[] remainingInSlot = new int[contents.length];

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            remainingInSlot[slot] =
                    item == null ? 0 : item.getAmount();
        }

        for (IngredientRequirement requirement : recipe.getRequirements()) {
            int remaining = requirement.getAmount();

            for (int slot = 0; slot < contents.length; slot++) {
                ItemStack item = contents[slot];

                if (item == null
                        || remainingInSlot[slot] <= 0
                        || !requirement.matches(item)) {
                    continue;
                }

                int taken = Math.min(
                        remaining,
                        remainingInSlot[slot]
                );

                remainingInSlot[slot] -= taken;
                remaining -= taken;

                if (remaining == 0) {
                    break;
                }
            }

            if (requirement == target) {
                return remaining;
            }
        }

        return 0;
    }

    /**
     * Allocates inventory quantities to individual requirements.
     * A quantity cannot be allocated twice.
     * Returns null if any requirement cannot be fulfilled.
     */
    int[][] createAllocation(
            Player player,
            LegendaryRecipe recipe
    ) {
        if (player == null || recipe == null) {
            return null;
        }

        ItemStack[] contents =
                player.getInventory().getStorageContents();

        int[] remainingInSlot = new int[contents.length];

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            remainingInSlot[slot] =
                    item == null ? 0 : item.getAmount();
        }

        List<IngredientRequirement> requirements =
                recipe.getRequirements();

        int[][] allocation =
                new int[requirements.size()][contents.length];

        for (int r = 0; r < requirements.size(); r++) {
            IngredientRequirement requirement = requirements.get(r);
            int remaining = requirement.getAmount();

            for (int slot = 0; slot < contents.length; slot++) {
                ItemStack item = contents[slot];

                if (item == null
                        || remainingInSlot[slot] <= 0
                        || !requirement.matches(item)) {
                    continue;
                }

                int taken = Math.min(
                        remaining,
                        remainingInSlot[slot]
                );

                allocation[r][slot] = taken;
                remainingInSlot[slot] -= taken;
                remaining -= taken;

                if (remaining == 0) {
                    break;
                }
            }

            if (remaining > 0) {
                return null;
            }
        }

        return allocation;
    }
}
