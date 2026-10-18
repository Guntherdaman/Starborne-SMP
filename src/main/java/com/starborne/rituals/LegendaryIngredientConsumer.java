
package com.starborne.rituals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class LegendaryIngredientConsumer {

    private final LegendaryIngredientChecker ingredientChecker;

    public LegendaryIngredientConsumer() {
        this.ingredientChecker = new LegendaryIngredientChecker();
    }

    /**
     * Removes recipe ingredients after confirmation.
     *
     * Returns false without changing the inventory if the player
     * does not have every required ingredient.
     */
    public boolean consumeIngredients(
            Player player,
            LegendaryRecipe recipe
    ) {
        if (player == null || recipe == null) {
            return false;
        }

        // Validate the complete recipe before modifying inventory.
        if (!ingredientChecker.hasIngredients(player, recipe)) {
            return false;
        }

        for (Map.Entry<Material, Integer> entry
                : recipe.getIngredients().entrySet()) {

            Material material = entry.getKey();
            int remaining = entry.getValue();

            for (ItemStack item
                    : player.getInventory().getStorageContents()) {

                if (item == null || item.getType() != material) {
                    continue;
                }

                int taken = Math.min(remaining, item.getAmount());

                item.setAmount(item.getAmount() - taken);
                remaining -= taken;

                if (remaining <= 0) {
                    break;
                }
            }

            // Defensive check: never report success if a requirement
            // could not be fulfilled.
            if (remaining > 0) {
                player.updateInventory();
                return false;
            }
        }

        player.updateInventory();
        return true;
    }
}
