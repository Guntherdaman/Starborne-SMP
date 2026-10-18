
package com.starborne.rituals;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LegendaryRecipeManager {

    private final Map<String, LegendaryRecipe> recipes =
            new LinkedHashMap<>();

    public boolean registerRecipe(LegendaryRecipe recipe) {
        if (recipe == null) {
            return false;
        }

        String id = recipe.getId().toLowerCase(
                java.util.Locale.ROOT
        );

        if (recipes.containsKey(id)) {
            return false;
        }

        recipes.put(id, recipe);
        return true;
    }

    public LegendaryRecipe getRecipe(String id) {
        if (id == null) {
            return null;
        }

        return recipes.get(
                id.toLowerCase(java.util.Locale.ROOT)
        );
    }

    public Collection<LegendaryRecipe> getRecipes() {
        return Collections.unmodifiableCollection(
                recipes.values()
        );
    }

    public boolean hasRecipes() {
        return !recipes.isEmpty();
    }

    public int getRecipeCount() {
        return recipes.size();
    }
}
