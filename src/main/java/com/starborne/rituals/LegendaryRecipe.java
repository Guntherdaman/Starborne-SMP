
package com.starborne.rituals;

import org.bukkit.Material;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LegendaryRecipe {

    private final String id;
    private final String displayName;
    private final Material icon;
    private final Map<Material, Integer> ingredients;

    public LegendaryRecipe(
            String id,
            String displayName,
            Material icon,
            Map<Material, Integer> ingredients
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipe ID cannot be empty."
            );
        }

        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipe display name cannot be empty."
            );
        }

        if (icon == null || icon.isAir()) {
            throw new IllegalArgumentException(
                    "Recipe icon must be a valid item."
            );
        }

        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException(
                    "A recipe must have at least one ingredient."
            );
        }

        Map<Material, Integer> ingredientCopy =
                new LinkedHashMap<>();

        for (Map.Entry<Material, Integer> entry
                : ingredients.entrySet()) {

            Material material = entry.getKey();
            Integer amount = entry.getValue();

            if (material == null
                    || material.isAir()
                    || amount == null
                    || amount <= 0) {
                throw new IllegalArgumentException(
                        "Recipe ingredients must have valid materials "
                                + "and positive amounts."
                );
            }

            ingredientCopy.put(material, amount);
        }

        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.ingredients = Collections.unmodifiableMap(
                ingredientCopy
        );
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public Map<Material, Integer> getIngredients() {
        return ingredients;
    }
}
