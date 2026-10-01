package com.starborne;

import com.starborne.abilities.AbilityManager;
import com.starborne.abilities.AbilityRegistry;
import com.starborne.commands.StarborneCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Starborne extends JavaPlugin {

    private AbilityManager abilityManager;
    private AbilityRegistry abilityRegistry;

    @Override
    public void onEnable() {
        getLogger().info("Starborne SMP has been enabled!");

        abilityManager = new AbilityManager();

        abilityRegistry = new AbilityRegistry();
        abilityRegistry.registerDefaults();

        getCommand("starborne").setExecutor(new StarborneCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("Starborne SMP has been disabled!");
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public AbilityRegistry getAbilityRegistry() {
        return abilityRegistry;
    }
}
