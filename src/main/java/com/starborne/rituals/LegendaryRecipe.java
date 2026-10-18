
package com.starborne.rituals;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LegendaryRecipe {

    private final String id;
    private final String displayName;
    private final Material icon;
    private final List<IngredientRequirement> requirements;
    private final Map<Material, Integer> ingredients;

    /**
     * Compatibility constructor for existing recipes.
     * Existing recipes continue to accept any item of the
     * specified material.
     */
    public LegendaryRecipe(
            String id,
            String displayName,
            Material icon,
            Map<Material, Integer> ingredients
    ) {
        this(
                id,
                displayName,
                icon,
                convertIngredients(ingredients)
        );
    }

    /**
     * Constructor for recipes with special ingredient requirements.
     */
    public LegendaryRecipe(
            String id,
            String displayName,
            Material icon,
            List<IngredientRequirement> requirements
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

        if (requirements == null || requirements.isEmpty()) {
            throw new IllegalArgumentException(
                    "A recipe must have at least one ingredient."
            );
        }

        List<IngredientRequirement> requirementCopy =
                new ArrayList<>();

        Map<Material, Integer> ingredientTotals =
                new LinkedHashMap<>();

        for (IngredientRequirement requirement : requirements) {
            if (requirement == null) {
                throw new IllegalArgumentException(
                        "Recipe requirements cannot contain null."
                );
            }

            requirementCopy.add(requirement);

            ingredientTotals.merge(
                    requirement.getMaterial(),
                    requirement.getAmount(),
                    Integer::sum
            );
        }

        this.id = id;
        this.displayName = displayName;
        this.icon = icon;

        this.requirements = Collections.unmodifiableList(
                requirementCopy
        );

        this.ingredients = Collections.unmodifiableMap(
                ingredientTotals
        );
    }

    private static List<IngredientRequirement> convertIngredients(
            Map<Material, Integer> ingredients
    ) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException(
                    "A recipe must have at least one ingredient."
            );
        }

        List<IngredientRequirement> converted = new ArrayList<>();

        for (Map.Entry<Material, Integer> entry
                : ingredients.entrySet()) {

            converted.add(
                    new IngredientRequirement(
                            entry.getKey(),
                            entry.getValue(),
                            entry.getValue() + "x "
                                    + entry.getKey().name()
                    )
            );
        }

        return converted;
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

    /**
     * Returns detailed requirements, including special item checks.
     */
    public List<IngredientRequirement> getRequirements() {
        return requirements;
    }

    /**
     * Compatibility view for existing menus and recipe code.
     * Materials shared by multiple requirements are combined here.
     */
    public Map<Material, Integer> getIngredients() {
        return ingredients;
    }
}
