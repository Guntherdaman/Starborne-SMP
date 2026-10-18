
package com.starborne.rituals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LegendaryIngredientChecker {

    public boolean hasIngredients(
            Player player,
            LegendaryRecipe recipe
    ) {
        return player != null
                && recipe != null
                && createAllocation(player, recipe) != null;
    }

    public int getMissingAmount(
            Player player,
            LegendaryRecipe recipe,
            Material material
    ) {
        if (player == null || recipe == null || material == null) {
            return 0;
        }

        int totalMissing = 0;

        for (IngredientRequirement requirement
                : recipe.getRequirements()) {

            if (requirement.getMaterial() != material) {
                continue;
            }

            int remaining = requirement.getAmount();

            for (ItemStack item
                    : player.getInventory().getStorageContents()) {

                if (item == null || !requirement.matches(item)) {
                    continue;
                }

                int available = item.getAmount();
                int taken = Math.min(remaining, available);
                remaining -= taken;

                if (remaining <= 0) {
                    break;
                }
            }

            totalMissing += remaining;
        }

        return totalMissing;
    }

    /**
     * Allocates separate inventory quantities to every requirement.
     * The same quantity cannot satisfy two requirements.
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
