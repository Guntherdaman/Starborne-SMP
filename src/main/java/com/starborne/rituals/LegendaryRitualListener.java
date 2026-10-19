
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class LegendaryRitualListener extends BukkitRunnable {

    private final Starborne plugin;
    private final LegendaryRitualManager ritualManager;
    private final LegendaryRecipeManager recipeManager;
    private final LegendaryItemFactory itemFactory;

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
    }

    @Override
    public void run() {
        for (Map.Entry<UUID, LegendaryRitualManager.Ritual> entry
                : ritualManager.getActiveRituals().entrySet()) {

            LegendaryRitualManager.Ritual ritual = entry.getValue();
            UUID ritualId = ritual.getId();
            Location location = ritual.getLocation();
            World world = location.getWorld();

            if (world == null) {
                ritualManager.removeRitual(ritualId);
                continue;
            }

            long remaining = ritual.getSecondsRemaining();

            if (remaining <= 0) {
                completeRitual(ritual, location, world);
                continue;
            }

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

        // Remove only after the recipe and reward have been validated.
        if (ritualManager.removeRitual(ritualId) == null) {
            return;
        }

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
}
