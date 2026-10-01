package com.starborne.abilities;

import com.starborne.abilities.abilities.TestAbility;

import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {

    private final Map<String, Ability> abilities = new HashMap<>();

    public void register(Ability ability) {
        abilities.put(ability.getName().toLowerCase(), ability);
    }

    public Ability get(String name) {
        return abilities.get(name.toLowerCase());
    }

    public Map<String, Ability> getAll() {
        return abilities;
    }

    public void registerDefaults() {
        register(new TestAbility());
    }
}
