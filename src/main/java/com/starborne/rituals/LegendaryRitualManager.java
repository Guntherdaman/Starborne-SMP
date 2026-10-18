
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LegendaryRitualManager {

    private final Starborne plugin;
    private final Map<UUID, Ritual> activeRituals = new HashMap<>();

    public LegendaryRitualManager(Starborne plugin) {
        this.plugin = plugin;
    }

    public boolean areRitualsEnabled() {
        return plugin.getConfig().getBoolean(
                "legendary.crafting-enabled", true
        ) && plugin.getConfig().getBoolean(
                "legendary.rituals-enabled", true
        );
    }

    public boolean isRitualActive(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        return activeRituals.values().stream().anyMatch(
                ritual -> ritual.getLocation().getWorld().equals(
                        location.getWorld()
                )
                && ritual.getLocation().getBlockX() == location.getBlockX()
                && ritual.getLocation().getBlockY() == location.getBlockY()
                && ritual.getLocation().getBlockZ() == location.getBlockZ()
        );
    }

    public boolean startRitual(Location location, UUID creator) {
        if (!areRitualsEnabled()
                || location == null
                || location.getWorld() == null
                || creator == null
                || isRitualActive(location)) {
            return false;
        }

        long duration = Math.max(
                1L,
                plugin.getConfig().getLong(
                        "legendary.ritual-duration-seconds", 900L
                )
        );

        Ritual ritual = new Ritual(
                UUID.randomUUID(),
                location.clone(),
                creator,
                System.currentTimeMillis() + duration * 1000L
        );

        activeRituals.put(ritual.getId(), ritual);

        return true;
    }

    public Ritual getRitualAt(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        for (Ritual ritual : activeRituals.values()) {
            Location ritualLocation = ritual.getLocation();

            if (ritualLocation.getWorld().equals(location.getWorld())
                    && ritualLocation.getBlockX() == location.getBlockX()
                    && ritualLocation.getBlockY() == location.getBlockY()
                    && ritualLocation.getBlockZ() == location.getBlockZ()) {
                return ritual;
            }
        }

        return null;
    }

    public Ritual removeRitual(UUID ritualId) {
        return activeRituals.remove(ritualId);
    }

    public Map<UUID, Ritual> getActiveRituals() {
        return Map.copyOf(activeRituals);
    }

    public static class Ritual {

        private final UUID id;
        private final Location location;
        private final UUID creator;
        private final long completionTimeMillis;

        public Ritual(
                UUID id,
                Location location,
                UUID creator,
                long completionTimeMillis
        ) {
            this.id = id;
            this.location = location.clone();
            this.creator = creator;
            this.completionTimeMillis = completionTimeMillis;
        }

        public UUID getId() {
            return id;
        }

        public Location getLocation() {
            return location.clone();
        }

        public UUID getCreator() {
            return creator;
        }

        public long getCompletionTimeMillis() {
            return completionTimeMillis;
        }

        public long getSecondsRemaining() {
            return Math.max(
                    0L,
                    (completionTimeMillis - System.currentTimeMillis() + 999L)
                            / 1000L
            );
        }
    }
}

