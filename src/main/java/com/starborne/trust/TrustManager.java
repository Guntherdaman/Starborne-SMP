
package com.starborne.trust;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class TrustManager {

    private final JavaPlugin plugin;
    private final File trustFile;

    private final Map<UUID, Set<UUID>> trustedPlayers =
            new HashMap<>();

    public TrustManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.trustFile = new File(
                plugin.getDataFolder(),
                "trust.yml"
        );

        load();
    }

    public boolean trust(UUID owner, UUID target) {
        if (owner.equals(target)) {
            return false;
        }

        boolean added = trustedPlayers
                .computeIfAbsent(
                        owner,
                        uuid -> new HashSet<>()
                )
                .add(target);

        if (added) {
            save();
        }

        return added;
    }

    public boolean untrust(UUID owner, UUID target) {
        Set<UUID> trusted = trustedPlayers.get(owner);

        if (trusted == null) {
            return false;
        }

        boolean removed = trusted.remove(target);

        if (trusted.isEmpty()) {
            trustedPlayers.remove(owner);
        }

        if (removed) {
            save();
        }

        return removed;
    }

    public boolean isTrusted(UUID owner, UUID target) {
        Set<UUID> trusted = trustedPlayers.get(owner);

        return trusted != null && trusted.contains(target);
    }

    public Set<UUID> getTrustedPlayers(UUID owner) {
        Set<UUID> trusted = trustedPlayers.get(owner);

        if (trusted == null) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(
                new HashSet<>(trusted)
        );
    }

    public void load() {
        trustedPlayers.clear();

        if (!trustFile.exists()) {
            return;
        }

        YamlConfiguration config =
                YamlConfiguration.loadConfiguration(trustFile);

        for (String ownerKey :
                config.getKeys(false)) {

            UUID owner;

            try {
                owner = UUID.fromString(ownerKey);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning(
                        "Skipping invalid trust owner UUID: "
                                + ownerKey
                );
                continue;
            }

            Set<UUID> trusted = new HashSet<>();

            for (String targetKey :
                    config.getStringList(ownerKey)) {

                try {
                    UUID target = UUID.fromString(targetKey);

                    if (!owner.equals(target)) {
                        trusted.add(target);
                    }
                } catch (IllegalArgumentException exception) {
                    plugin.getLogger().warning(
                            "Skipping invalid trusted UUID: "
                                    + targetKey
                    );
                }
            }

            if (!trusted.isEmpty()) {
                trustedPlayers.put(owner, trusted);
            }
        }

        plugin.getLogger().info(
                "Loaded trust data for "
                        + trustedPlayers.size()
                        + " players."
        );
    }

    public void save() {
        if (!plugin.getDataFolder().exists()
                && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().severe(
                    "Could not create Starborne data folder."
            );
            return;
        }

        YamlConfiguration config =
                new YamlConfiguration();

        for (Map.Entry<UUID, Set<UUID>> entry :
                trustedPlayers.entrySet()) {

            config.set(
                    entry.getKey().toString(),
                    entry.getValue().stream()
                            .map(UUID::toString)
                            .toList()
            );
        }

        try {
            config.save(trustFile);
        } catch (IOException exception) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "Could not save trust data!",
                    exception
            );
        }
    }
}

