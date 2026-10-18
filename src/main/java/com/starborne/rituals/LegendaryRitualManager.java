
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Location;

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
                ritual -> sameBlock(ritual.getLocation(), location)
        );
    }

    /**
     * Backward-compatible method for the existing test command.
     * Test rituals do not have a recipe attached.
     */
    public boolean startRitual(Location location, UUID creator) {
        return startRitual(location, creator, null);
    }

    /**
     * Starts a ritual for a particular Legendary recipe.
     * A null recipe ID is allowed only for the existing test command.
     */
    public boolean startRitual(
            Location location,
            UUID creator,
            String recipeId
    ) {
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
                location,
                creator,
                recipeId,
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
            if (sameBlock(ritual.getLocation(), location)) {
                return ritual;
            }
        }

        return null;
    }

    public Ritual getRitual(UUID ritualId) {
        return activeRituals.get(ritualId);
    }

    public Ritual removeRitual(UUID ritualId) {
        return activeRituals.remove(ritualId);
    }

    public Map<UUID, Ritual> getActiveRituals() {
        return Map.copyOf(activeRituals);
    }

    private boolean sameBlock(Location first, Location second) {
        return first.getWorld() != null
                && second.getWorld() != null
                && first.getWorld().getUID().equals(
                        second.getWorld().getUID()
                )
                && first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY()
                && first.getBlockZ() == second.getBlockZ();
    }

    public static class Ritual {

        private final UUID id;
        private final Location location;
        private final UUID creator;
        private final String recipeId;
        private final long completionTimeMillis;

        public Ritual(
                UUID id,
                Location location,
                UUID creator,
                long completionTimeMillis
        ) {
            this(id, location, creator, null, completionTimeMillis);
        }

        public Ritual(
                UUID id,
                Location location,
                UUID creator,
                String recipeId,
                long completionTimeMillis
        ) {
            this.id = id;
            this.location = location.clone();
            this.creator = creator;
            this.recipeId = recipeId;
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

        /**
         * Returns the ID of the Legendary recipe being crafted.
         * Returns null for a test ritual started without a recipe.
         */
        public String getRecipeId() {
            return recipeId;
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
