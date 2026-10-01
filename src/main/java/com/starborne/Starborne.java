package com.starborne;

import org.bukkit.plugin.java.JavaPlugin;

public final class Starborne extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Starborne SMP has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Starborne SMP has been disabled!");
    }
}
