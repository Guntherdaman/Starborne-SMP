
package com.starborne.rituals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LegendaryIngredientConsumer {

    private final LegendaryIngredientChecker ingredientChecker;

    public LegendaryIngredientConsumer() {
        this.ingredientChecker = new LegendaryIngredientChecker();
    }

    /**
     * Removes the exact item quantities allocated by the ingredient
     * checker, including ingredients with special item requirements.
     *
     * Returns false without changing the inventory if the requirements
     * cannot be fulfilled or the allocation is invalid.
     */
    public boolean consumeIngredients(
            Player player,
            LegendaryRecipe recipe
    ) {
        if (player == null || recipe == null) {
            return false;
        }

        List<IngredientRequirement> requirements =
                recipe.getRequirements();

        if (requirements == null) {
            return false;
        }

        // Create one allocation for both validation and consumption.
        int[][] allocation = ingredientChecker.createAllocation(
                player,
                recipe
        );

        if (allocation == null
                || allocation.length != requirements.size()) {
            return false;
        }

        ItemStack[] contents =
                player.getInventory().getStorageContents();

        // Track the total amount removed from each inventory slot.
        int[] totalToRemove = new int[contents.length];

        // Validate the complete allocation before changing any items.
        for (int requirementIndex = 0;
             requirementIndex < requirements.size();
             requirementIndex++) {

            IngredientRequirement requirement =
                    requirements.get(requirementIndex);

            if (requirement == null
                    || allocation[requirementIndex] == null
                    || allocation[requirementIndex].length
                            != contents.length) {
                return false;
            }

            int allocatedForRequirement = 0;

            for (int slot = 0; slot < contents.length; slot++) {
                int amount = allocation[requirementIndex][slot];

                if (amount < 0) {
                    return false;
                }

                if (amount == 0) {
                    continue;
                }

                ItemStack item = contents[slot];

                if (item == null || !requirement.matches(item)) {
                    return false;
                }

                allocatedForRequirement += amount;
                totalToRemove[slot] += amount;

                // The combined allocation must never exceed the stack.
                if (totalToRemove[slot] > item.getAmount()) {
                    return false;
                }
            }

            // Ensure the allocation fulfils the entire requirement.
            if (allocatedForRequirement != requirement.getAmount()) {
                return false;
            }
        }

        // All requirements passed validation. Consume each slot once.
        for (int slot = 0; slot < contents.length; slot++) {
            int amountToRemove = totalToRemove[slot];

            if (amountToRemove <= 0) {
                continue;
            }

            ItemStack item = contents[slot];

            if (item == null) {
                // Defensive safeguard; nothing has been committed yet
                // to the player's inventory.
                return false;
            }

            int remaining = item.getAmount() - amountToRemove;

            if (remaining <= 0) {
                contents[slot] = null;
            } else {
                item.setAmount(remaining);
            }
        }

        player.getInventory().setStorageContents(contents);
        player.updateInventory();

        return true;
    }
}
