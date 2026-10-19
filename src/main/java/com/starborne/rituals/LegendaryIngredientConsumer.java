
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
     * Returns false without changing inventory if requirements
     * cannot be fulfilled.
     */
    public boolean consumeIngredients(
            Player player,
            LegendaryRecipe recipe
    ) {
        if (player == null || recipe == null) {
            return false;
        }

        // Build one allocation so validation and consumption agree
        // about which inventory stacks satisfy each requirement.
        int[][] allocation = ingredientChecker.createAllocation(
                player,
                recipe
        );

        if (allocation == null) {
            return false;
        }

        ItemStack[] contents =
                player.getInventory().getStorageContents();

        List<IngredientRequirement> requirements =
                recipe.getRequirements();

        // Consume only the stacks allocated to each requirement.
        for (int requirementIndex = 0;
             requirementIndex < requirements.size();
             requirementIndex++) {

            for (int slot = 0; slot < contents.length; slot++) {
                int amountToRemove =
                        allocation[requirementIndex][slot];

                if (amountToRemove <= 0) {
                    continue;
                }

                ItemStack item = contents[slot];

                // Defensive check in case inventory contents changed.
                if (item == null
                        || item.getAmount() < amountToRemove
                        || !requirements.get(requirementIndex)
                                .matches(item)) {
                    return false;
                }
            }
        }

        // All allocated stacks are validated before any are changed.
        for (int requirementIndex = 0;
             requirementIndex < requirements.size();
             requirementIndex++) {

            for (int slot = 0; slot < contents.length; slot++) {
                int amountToRemove =
                        allocation[requirementIndex][slot];

                if (amountToRemove <= 0) {
                    continue;
                }

                ItemStack item = contents[slot];
                int remaining = item.getAmount() - amountToRemove;

                if (remaining <= 0) {
                    contents[slot] = null;
                } else {
                    item.setAmount(remaining);
                }
            }
        }

        player.getInventory().setStorageContents(contents);
        player.updateInventory();

        return true;
    }
}
