
package com.starborne.souls;

import com.starborne.Starborne;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvPTierManager {

    private final Starborne plugin;
    private final File file;
    private final Map<UUID, PvPTier> tiers = new HashMap<>();
    private final Map<UUID, Integer> progress = new HashMap<>();

    public PvPTierManager(Starborne plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "souls.yml");

        load();
    }

    public PvPTier getTier(UUID playerId) {
        return tiers.getOrDefault(playerId, PvPTier.F);
    }

    public void setTier(UUID playerId, PvPTier tier) {
        if (playerId == null || tier == null) {
            throw new IllegalArgumentException(
                    "Player UUID and tier cannot be null."
            );
        }

        tiers.put(playerId, tier);
        progress.remove(playerId);
        save();
    }

    public int getProgress(UUID playerId) {
        return progress.getOrDefault(playerId, 0);
    }

    public PvPTier recordKill(UUID playerId) {
        return recordResult(playerId, 1);
    }

    public PvPTier recordDeath(UUID playerId) {
        return recordResult(playerId, -1);
    }

    private PvPTier recordResult(UUID playerId, int result) {
        if (playerId == null) {
            throw new IllegalArgumentException(
                    "Player UUID cannot be null."
            );
        }

        int newProgress = getProgress(playerId) + result;

        if (newProgress >= 3) {
            PvPTier currentTier = getTier(playerId);

            if (currentTier != PvPTier.S) {
                tiers.put(playerId, currentTier.getNextTier());
            }

            progress.remove(playerId);
        } else if (newProgress <= -3) {
            PvPTier currentTier = getTier(playerId);

            if (currentTier != PvPTier.F) {
                tiers.put(playerId, currentTier.getPreviousTier());
            }

            progress.remove(playerId);
        } else {
            progress.put(playerId, newProgress);
        }

        save();
        return getTier(playerId);
    }

    public void load() {
        tiers.clear();
        progress.clear();

        if (!file.exists()) {
            return;
        }

        YamlConfiguration config =
                YamlConfiguration.loadConfiguration(file);

        ConfigurationSection players =
                config.getConfigurationSection("players");

        if (players == null) {
            return;
        }

        for (String key : players.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                String tierName = config.getString(
                        "players." + key + ".tier",
                        "F"
                );

                PvPTier tier = PvPTier.valueOf(
                        tierName.toUpperCase()
                );

                int playerProgress = config.getInt(
                        "players." + key + ".progress",
                        0
                );

                tiers.put(playerId, tier);

                if (playerProgress != 0) {
                    progress.put(
                            playerId,
                            Math.max(-2, Math.min(2, playerProgress))
                    );
                }
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning(
                        "Skipping invalid soul data for " + key
                );
            }
        }

        plugin.getLogger().info(
                "Loaded soul data for " + tiers.size() + " players."
        );
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, PvPTier> entry : tiers.entrySet()) {
            String key = entry.getKey().toString();

            config.set("players." + key + ".tier",
                    entry.getValue().name());

            config.set("players." + key + ".progress",
                    getProgress(entry.getKey()));
        }

        for (Map.Entry<UUID, Integer> entry : progress.entrySet()) {
            String key = entry.getKey().toString();

            config.set("players." + key + ".tier",
                    getTier(entry.getKey()).name());

            config.set("players." + key + ".progress",
                    entry.getValue());
        }

        try {
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()
                    && !parent.mkdirs()) {
                plugin.getLogger().severe(
                        "Could not create the Starborne data folder!"
                );
                return;
            }

            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe(
                    "Could not save soul data: "
                            + exception.getMessage()
            );
        }
    }
}
