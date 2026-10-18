
package com.starborne.rituals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class LegendaryIngredientChecker {

    public boolean hasIngredients(
            Player player,
            LegendaryRecipe recipe
    ) {
        if (player == null || recipe == null) {
            return false;
        }

        Map<Material, Integer> required =
                recipe.getIngredients();

        for (Map.Entry<Material, Integer> entry
                : required.entrySet()) {

            Material material = entry.getKey();
            int requiredAmount = entry.getValue();
            int availableAmount = 0;

            for (ItemStack item
                    : player.getInventory().getStorageContents()) {

                if (item == null || item.getType() != material) {
                    continue;
                }

                availableAmount += item.getAmount();

                if (availableAmount >= requiredAmount) {
                    break;
                }
            }

            if (availableAmount < requiredAmount) {
                return false;
            }
        }

        return true;
    }

    public int getMissingAmount(
            Player player,
            LegendaryRecipe recipe,
            Material material
    ) {
        if (player == null || recipe == null || material == null) {
            return 0;
        }

        int requiredAmount = recipe.getIngredients()
                .getOrDefault(material, 0);

        int availableAmount = 0;

        for (ItemStack item
                : player.getInventory().getStorageContents()) {

            if (item != null && item.getType() == material) {
                availableAmount += item.getAmount();
            }
        }

        return Math.max(0, requiredAmount - availableAmount);
    }
}
