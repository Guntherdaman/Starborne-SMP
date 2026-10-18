
package com.starborne.rituals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public final class IngredientRequirement {

    private final Material material;
    private final int amount;
    private final String description;
    private final Predicate<ItemStack> specialMatcher;

    /**
     * Creates a basic ingredient requirement.
     * Any item of the specified material will match.
     */
    public IngredientRequirement(
            Material material,
            int amount,
            String description
    ) {
        this(material, amount, description, item -> true);
    }

    /**
     * Creates an ingredient requirement with an optional
     * special matcher for enchantments, potion effects,
     * firework properties, or other item metadata.
     */
    public IngredientRequirement(
            Material material,
            int amount,
            String description,
            Predicate<ItemStack> specialMatcher
    ) {
        if (material == null || material.isAir()) {
            throw new IllegalArgumentException(
                    "Ingredient material must be valid."
            );
        }

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Ingredient amount must be positive."
            );
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Ingredient description cannot be empty."
            );
        }

        this.material = material;
        this.amount = amount;
        this.description = description;
        this.specialMatcher = Objects.requireNonNull(
                specialMatcher,
                "Special matcher cannot be null."
        );
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks both the material and any special item properties.
     */
    public boolean matches(ItemStack item) {
        if (item == null || item.getType() != material) {
            return false;
        }

        return specialMatcher.test(item.clone());
    }
}
