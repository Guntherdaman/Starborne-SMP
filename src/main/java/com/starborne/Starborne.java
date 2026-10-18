
package com.starborne;

import com.starborne.abilities.AbilityItem;
import com.starborne.abilities.AbilityManager;
import com.starborne.abilities.AbilityRegistry;
import com.starborne.commands.AbilitiesCommand;
import com.starborne.commands.AbilityCommand;
import com.starborne.commands.SetAbilityCommand;
import com.starborne.commands.StarborneCommand;
import com.starborne.trust.TrustManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.starborne.commands.TrustCommand;
import com.starborne.commands.SoulsCommand;
import com.starborne.souls.PvPTierManager;

public final class Starborne extends JavaPlugin {

    private AbilityManager abilityManager;
    private AbilityRegistry abilityRegistry;
    private AbilityItem abilityItem;
    private TrustManager trustManager;
    private PvPTierManager pvPTierManager;
    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("Starborne SMP has been enabled!");

        abilityManager = new AbilityManager();
        abilityRegistry = new AbilityRegistry();
        abilityRegistry.registerDefaults();

        abilityItem = new AbilityItem(this);
        trustManager = new TrustManager(this);
        pvPTierManager = new PvPTierManager();

        getCommand("starborne").setExecutor(
                new StarborneCommand()
        );

        AbilityCommand abilityCommand =
                new AbilityCommand(this);

        getCommand("ability1").setExecutor(abilityCommand);
        getCommand("ability2").setExecutor(abilityCommand);
        getCommand("ability3").setExecutor(abilityCommand);
        getCommand("ability4").setExecutor(abilityCommand);
        getCommand("ability5").setExecutor(abilityCommand);

        getCommand("setability").setExecutor(
                new SetAbilityCommand(this)
        );

        getCommand("abilities").setExecutor(
                new AbilitiesCommand(this)
        );
        
getCommand("trust").setExecutor(
        new TrustCommand(this)
);
        getCommand("souls").setExecutor(
        new SoulsCommand(this)
);

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

    public AbilityItem getAbilityItem() {
        return abilityItem;
    }

    public TrustManager getTrustManager() {
        return trustManager;
        public PvPTierManager getPvPTierManager() {
    return pvPTierManager;
}
    }
}
