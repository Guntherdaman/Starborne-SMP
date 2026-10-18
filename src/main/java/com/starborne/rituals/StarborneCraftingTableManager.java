
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StarborneCraftingTableManager {

    private final Starborne plugin;
    private final File dataFile;
    private final Set<String> registeredTables = new HashSet<>();

    public StarborneCraftingTableManager(Starborne plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "tables.yml");

        loadTables();
    }

    public void registerTable(Location location) {
        if (!isValidLocation(location)) {
            return;
        }

        registeredTables.add(toKey(location));
        saveTables();
    }

    public void unregisterTable(Location location) {
        if (!isValidLocation(location)) {
            return;
        }

        registeredTables.remove(toKey(location));
        saveTables();
    }

    public boolean isRegisteredTable(Location location) {
        if (!isValidLocation(location)) {
            return false;
        }

        return registeredTables.contains(toKey(location));
    }

    private String toKey(Location location) {
        return location.getWorld().getUID()
                + ":"
                + location.getBlockX()
                + ":"
                + location.getBlockY()
                + ":"
                + location.getBlockZ();
    }

    private boolean isValidLocation(Location location) {
        return location != null && location.getWorld() != null;
    }

    private void loadTables() {
        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration data =
                YamlConfiguration.loadConfiguration(dataFile);

        registeredTables.addAll(
                data.getStringList("tables")
        );

        plugin.getLogger().info(
                "Loaded " + registeredTables.size()
                        + " Starborne crafting table locations."
        );
    }

    private void saveTables() {
        if (!plugin.getDataFolder().exists()
                && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().severe(
                    "Could not create the Starborne data folder!"
            );
            return;
        }

        YamlConfiguration data = new YamlConfiguration();

        data.set("tables", registeredTables.stream().sorted().toList());

        try {
            data.save(dataFile);
        } catch (IOException exception) {
            plugin.getLogger().severe(
                    "Could not save Starborne crafting tables: "
                            + exception.getMessage()
            );
        }
    }
}
