package com.starborne;

import com.starborne.abilities.AbilityManager;
import com.starborne.abilities.AbilityRegistry;
import com.starborne.commands.AbilityCommand;
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

        AbilityCommand abilityCommand = new AbilityCommand(this);

        getCommand("ability1").setExecutor(abilityCommand);
        getCommand("ability2").setExecutor(abilityCommand);
        getCommand("ability3").setExecutor(abilityCommand);
        getCommand("ability4").setExecutor(abilityCommand);
        getCommand("ability5").setExecutor(abilityCommand);
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
