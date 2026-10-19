
package com.starborne.rituals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
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
     * Returns the combined missing amounts for requirements
     * that use the requested material.
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
     * Returns the missing amount for one requirement,
     * accounting for inventory quantities allocated to
     * the other requirements.
     */
    public int getMissingAmount(
            Player player,
            LegendaryRecipe recipe,
            IngredientRequirement target
    ) {
        if (player == null || recipe == null || target == null) {
            return 0;
        }

        int[][] allocation = createAllocation(player, recipe);

        if (allocation == null) {
            // If the full recipe cannot be fulfilled, calculate
            // the target's shortfall using shared inventory counts.
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
                            remaining, remainingInSlot[slot]
                    );

                    remainingInSlot[slot] -= taken;
                    remaining -= taken;
                }

                if (requirement == target) {
                    return remaining;
                }
            }

            return 0;
        }

        List<IngredientRequirement> requirements =
                recipe.getRequirements();

        for (int r = 0; r < requirements.size(); r++) {
            if (requirements.get(r) == target) {
                int supplied = 0;

                for (int amount : allocation[r]) {
                    supplied += amount;
                }

                return Math.max(0, target.getAmount() - supplied);
            }
        }

        return 0;
    }

    /**
     * Allocates each inventory quantity at most once.
     * More restrictive requirements are allocated first.
     * Returns null if the inventory cannot fulfill the recipe.
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
                new ArrayList<>(recipe.getRequirements());

        // Allocate restrictive matches before generic material matches.
        requirements.sort(
                Comparator.comparingInt(
                        (IngredientRequirement requirement) ->
                                countMatchingStacks(contents, requirement)
                ).thenComparing(
                        Comparator.comparingInt(
                                IngredientRequirement::getAmount
                        ).reversed()
                )
        );

        int[][] allocation =
                new int[recipe.getRequirements().size()][contents.length];

        for (IngredientRequirement requirement : requirements) {
            int originalIndex = recipe.getRequirements().indexOf(requirement);
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

                allocation[originalIndex][slot] += taken;
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

    private int countMatchingStacks(
            ItemStack[] contents,
            IngredientRequirement requirement
    ) {
        int count = 0;

        for (ItemStack item : contents) {
            if (item != null
                    && item.getAmount() > 0
                    && requirement.matches(item)) {
                count++;
            }
        }

        return count;
    }
}
