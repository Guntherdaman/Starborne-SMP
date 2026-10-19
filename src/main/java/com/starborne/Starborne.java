
package com.starborne;

import com.starborne.abilities.AbilityItem;
import com.starborne.abilities.AbilityManager;
import com.starborne.abilities.AbilityRegistry;
import com.starborne.commands.AbilitiesCommand;
import com.starborne.commands.AbilityCommand;
import com.starborne.commands.SetAbilityCommand;
import com.starborne.commands.StarborneCommand;
import com.starborne.commands.TrustCommand;
import com.starborne.commands.SoulsCommand;
import com.starborne.commands.PvPTierCommand;
import com.starborne.commands.SoulPowerCommand;
import com.starborne.commands.RitualCommand;
import com.starborne.rituals.FirstWaveRecipes;
import com.starborne.rituals.LegendaryRitualManager;
import com.starborne.rituals.LegendaryRitualListener;
import com.starborne.rituals.LegendaryRecipeManager;
import com.starborne.rituals.LegendaryRecipeMenu;
import com.starborne.rituals.LegendaryRitualConfirmationMenu;
import com.starborne.rituals.StarborneCraftingTable;
import com.starborne.rituals.StarborneCraftingTableManager;
import com.starborne.rituals.StarborneCraftingTableListener;
import com.starborne.souls.PvPTierManager;
import com.starborne.souls.PvPTierListener;
import com.starborne.souls.SoulStarListener;
import com.starborne.souls.SoulPowerManager;
import com.starborne.souls.SoulPowerListener;
import com.starborne.trust.TrustManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Starborne extends JavaPlugin {

    private AbilityManager abilityManager;
    private AbilityRegistry abilityRegistry;
    private AbilityItem abilityItem;
    private TrustManager trustManager;
    private PvPTierManager pvPTierManager;
    private SoulPowerManager soulPowerManager;
    private LegendaryRitualManager legendaryRitualManager;
    private LegendaryRitualListener legendaryRitualListener;

    private StarborneCraftingTable starborneCraftingTable;
    private StarborneCraftingTableManager craftingTableManager;
    private LegendaryRecipeManager legendaryRecipeManager;
    private LegendaryRecipeMenu legendaryRecipeMenu;
    private LegendaryRitualConfirmationMenu confirmationMenu;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("Starborne SMP has been enabled!");

        abilityManager = new AbilityManager();
        abilityRegistry = new AbilityRegistry();
        abilityRegistry.registerDefaults();

        abilityItem = new AbilityItem(this);
        trustManager = new TrustManager(this);
        pvPTierManager = new PvPTierManager(this);
        soulPowerManager = new SoulPowerManager(this);

        legendaryRitualManager = new LegendaryRitualManager(this);

        // Initialize the custom Starborne crafting table.
        starborneCraftingTable = new StarborneCraftingTable(this);
        craftingTableManager = new StarborneCraftingTableManager(this);
        starborneCraftingTable.registerRecipe();

        // Initialize and register all first-wave Legendary recipes.
        legendaryRecipeManager = new LegendaryRecipeManager();
        FirstWaveRecipes.registerAll(this, legendaryRecipeManager);

        getLogger().info(
                "Registered " + legendaryRecipeManager.getRecipeCount()
                        + " first-wave Legendary recipes."
        );

        // Initialize the ritual confirmation menu.
        confirmationMenu = new LegendaryRitualConfirmationMenu(
                this,
                legendaryRecipeManager
        );

        // Initialize the recipe menu and connect confirmation.
        legendaryRecipeMenu = new LegendaryRecipeMenu(
                this,
                legendaryRecipeManager,
                confirmationMenu
        );

        getServer().getPluginManager().registerEvents(
                legendaryRecipeMenu,
                this
        );

        getServer().getPluginManager().registerEvents(
                confirmationMenu,
                this
        );

        getServer().getPluginManager().registerEvents(
                new StarborneCraftingTableListener(
                        this,
                        starborneCraftingTable,
                        craftingTableManager,
                        legendaryRecipeMenu
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PvPTierListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new SoulStarListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new SoulPowerListener(this),
                this
        );

        legendaryRitualListener = new LegendaryRitualListener(
                this,
                legendaryRitualManager
        );

        legendaryRitualListener.runTaskTimer(
                this,
                20L,
                20L
        );

        getCommand("starborne").setExecutor(
                new StarborneCommand()
        );

        AbilityCommand abilityCommand = new AbilityCommand(this);

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

        getCommand("pvptier").setExecutor(
                new PvPTierCommand(this)
        );

        getCommand("soulpower").setExecutor(
                new SoulPowerCommand(this)
        );

        getCommand("ritualstart").setExecutor(
                new RitualCommand(this, legendaryRitualManager)
        );
    }

    @Override
    public void onDisable() {
        if (legendaryRitualListener != null) {
            legendaryRitualListener.cancel();
        }

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
    }

    public PvPTierManager getPvPTierManager() {
        return pvPTierManager;
    }

    public SoulPowerManager getSoulPowerManager() {
        return soulPowerManager;
    }

    public LegendaryRitualManager getLegendaryRitualManager() {
        return legendaryRitualManager;
    }

    public StarborneCraftingTable getStarborneCraftingTable() {
        return starborneCraftingTable;
    }

    public StarborneCraftingTableManager getCraftingTableManager() {
        return craftingTableManager;
    }

    public LegendaryRecipeManager getLegendaryRecipeManager() {
        return legendaryRecipeManager;
    }

    public LegendaryRecipeMenu getLegendaryRecipeMenu() {
        return legendaryRecipeMenu;
    }

    public LegendaryRitualConfirmationMenu getConfirmationMenu() {
        return confirmationMenu;
    }
}
