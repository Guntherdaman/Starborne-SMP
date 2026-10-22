
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LegendaryRitualListener extends BukkitRunnable {

    private final Starborne plugin;
    private final LegendaryRitualManager ritualManager;
    private final LegendaryRecipeManager recipeManager;
    private final LegendaryItemFactory itemFactory;
    private final LegendaryRitualVisual ritualVisual;

    private final Set<UUID> visualizedRituals = new HashSet<>();

    public LegendaryRitualListener(
            Starborne plugin,
            LegendaryRitualManager ritualManager
    ) {
        if (plugin == null || ritualManager == null) {
            throw new IllegalArgumentException(
                    "Plugin and ritual manager cannot be null."
            );
        }

        this.plugin = plugin;
        this.ritualManager = ritualManager;
        this.recipeManager = plugin.getLegendaryRecipeManager();
        this.itemFactory = new LegendaryItemFactory(plugin);
        this.ritualVisual = new LegendaryRitualVisual(plugin);
    }

    @Override
    public void run() {
        Map<UUID, LegendaryRitualManager.Ritual> activeRituals =
                ritualManager.getActiveRituals();

        for (Map.Entry<UUID, LegendaryRitualManager.Ritual> entry
                : activeRituals.entrySet()) {

            LegendaryRitualManager.Ritual ritual = entry.getValue();
            UUID ritualId = ritual.getId();
            Location location = ritual.getLocation();
            World world = location.getWorld();

            if (world == null) {
                ritualManager.removeRitual(ritualId);
                removeVisual(ritualId);
                continue;
            }

            long remaining = ritual.getSecondsRemaining();

            if (remaining <= 0) {
                completeRitual(ritual, location, world);
                continue;
            }

            // Show a floating preview for real Legendary recipes.
            // Test rituals without a recipe do not receive a visual.
            startVisualIfNeeded(ritual, location);

            long totalDuration = Math.max(
                    1L,
                    plugin.getConfig().getLong(
                            "legendary.ritual-duration-seconds",
                            900L
                    )
            );

            long leakInterval = Math.max(
                    1L,
                    plugin.getConfig().getLong(
                            "legendary.coordinate-leak-interval-seconds",
                            300L
                    )
            );

            long elapsed = totalDuration - remaining;

            if (elapsed > 0 && elapsed % leakInterval == 0) {
                Bukkit.broadcastMessage(
                        ChatColor.DARK_PURPLE
                                + "⚠ LEGENDARY RITUAL COORDINATES ⚠"
                );

                Bukkit.broadcastMessage(
                        ChatColor.LIGHT_PURPLE
                                + "World: " + world.getName()
                                + " | X: " + location.getBlockX()
                                + " Y: " + location.getBlockY()
                                + " Z: " + location.getBlockZ()
                );

                Bukkit.broadcastMessage(
                        ChatColor.GRAY
                                + "Time remaining: "
                                + formatTime(remaining)
                );
            }
        }

        // Clean up previews for rituals removed by other code,
        // including rituals cancelled outside this listener.
        for (UUID ritualId : new HashSet<>(visualizedRituals)) {
            if (!ritualManager.getActiveRituals().containsKey(ritualId)) {
                removeVisual(ritualId);
            }
        }
    }

    private void startVisualIfNeeded(
            LegendaryRitualManager.Ritual ritual,
            Location location
    ) {
        UUID ritualId = ritual.getId();

        if (visualizedRituals.contains(ritualId)) {
            return;
        }

        String recipeId = ritual.getRecipeId();

        if (recipeId == null || recipeManager == null) {
            return;
        }

        LegendaryRecipe recipe = recipeManager.getRecipe(recipeId);

        if (recipe == null) {
            return;
        }

        ItemStack preview = new ItemStack(recipe.getIcon());

        ritualVisual.start(
                ritualId,
                location,
                preview
        );

        visualizedRituals.add(ritualId);

        plugin.getLogger().info(
                "Started floating visual for Legendary ritual "
                        + ritualId + " (" + recipeId + ")."
        );
    }

    private void removeVisual(UUID ritualId) {
        ritualVisual.remove(ritualId);
        visualizedRituals.remove(ritualId);
    }

    private void completeRitual(
            LegendaryRitualManager.Ritual ritual,
            Location location,
            World world
    ) {
        UUID ritualId = ritual.getId();
        String recipeId = ritual.getRecipeId();

        // Test rituals have no recipe and must not create an item.
        if (recipeId == null) {
            ritualManager.removeRitual(ritualId);
            removeVisual(ritualId);

            plugin.getLogger().info(
                    "A test ritual completed without a reward recipe."
            );
            return;
        }

        LegendaryRecipe recipe = recipeManager == null
                ? null
                : recipeManager.getRecipe(recipeId);

        if (recipe == null) {
            ritualManager.removeRitual(ritualId);
            removeVisual(ritualId);

            plugin.getLogger().severe(
                    "Could not reward completed ritual " + ritualId
                            + ": unknown recipe ID " + recipeId
            );
            return;
        }

        final ItemStack reward;

        try {
            reward = itemFactory.create(recipe);
        } catch (RuntimeException exception) {
            plugin.getLogger().severe(
                    "Failed to create reward for recipe "
                            + recipeId + ": " + exception.getMessage()
            );
            return;
        }

        // Remove the ritual and its preview only after the reward
        // has been successfully created.
        if (ritualManager.removeRitual(ritualId) == null) {
            removeVisual(ritualId);
            return;
        }

        removeVisual(ritualId);

        Player creator = Bukkit.getPlayer(ritual.getCreator());

        if (creator != null && creator.isOnline()) {
            Map<Integer, ItemStack> leftovers =
                    creator.getInventory().addItem(reward);

            for (ItemStack leftover : leftovers.values()) {
                world.dropItemNaturally(
                        creator.getLocation(),
                        leftover
                );
            }

            creator.sendMessage(
                    ChatColor.LIGHT_PURPLE
                            + "✦ Your Legendary is complete: "
                            + ChatColor.WHITE + recipe.getDisplayName()
                            + ChatColor.LIGHT_PURPLE + "!"
            );
        } else {
            world.dropItemNaturally(
                    location.clone().add(0.5, 1.0, 0.5),
                    reward
            );
        }

        Bukkit.broadcastMessage(
                ChatColor.LIGHT_PURPLE
                        + "✦ The Legendary "
                        + ChatColor.WHITE + recipe.getDisplayName()
                        + ChatColor.LIGHT_PURPLE
                        + " has been crafted!"
        );

        Bukkit.broadcastMessage(
                ChatColor.GRAY
                        + "Location: " + world.getName()
                        + " " + location.getBlockX()
                        + ", " + location.getBlockY()
                        + ", " + location.getBlockZ()
        );
    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                remainingSeconds
        );
    }

    /**
     * Call during plugin shutdown to remove all ritual previews.
     */
    public void cleanupVisuals() {
        ritualVisual.removeAll();
        visualizedRituals.clear();
    }
}
