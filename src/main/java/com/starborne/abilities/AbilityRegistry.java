
package com.starborne.abilities;

import com.starborne.abilities.abilities.TestAbility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {

    private final Map<String, Ability> abilities = new HashMap<>();

    public void register(String id, Ability ability) {
        abilities.put(id.toLowerCase(), ability);
    }

    public Ability get(String id) {
        if (id == null) {
            return null;
        }

        return abilities.get(id.toLowerCase());
    }

    public Map<String, Ability> getAll() {
        return Collections.unmodifiableMap(abilities);
    }

    public void registerDefaults() {
        register("test_ability", new TestAbility());
    }
}
