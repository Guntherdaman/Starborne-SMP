
package com.starborne.rituals;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LegendaryIngredientConsumer {

    private final LegendaryIngredientChecker ingredientChecker;

    public LegendaryIngredientConsumer() {
        this.ingredientChecker = new LegendaryIngredientChecker();
    }

    /**
     * Consumes the exact inventory quantities allocated to each
     * recipe requirement, including special item metadata checks.
     *
     * Returns false without changing inventory if the complete
     * recipe cannot be satisfied.
     */
    public boolean consumeIngredients(
            Player player,
            LegendaryRecipe recipe
    ) {
        if (player == null || recipe == null) {
            return false;
        }

        // Calculate the complete allocation before changing anything.
        int[][] allocation =
                ingredientChecker.createAllocation(player, recipe);

        if (allocation == null) {
            return false;
        }

        ItemStack[] contents =
                player.getInventory().getStorageContents();

        // Work out how much to remove from each inventory slot.
        for (int slot = 0; slot < contents.length; slot++) {
            int totalToRemove = 0;

            for (int requirement = 0;
                    requirement < allocation.length;
                    requirement++) {
                totalToRemove += allocation[requirement][slot];
            }

            if (totalToRemove <= 0) {
                continue;
            }

            ItemStack item = contents[slot];

            // Defensive validation before modifying the inventory.
            if (item == null || item.getAmount() < totalToRemove) {
                return false;
            }

            int remaining = item.getAmount() - totalToRemove;

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
