
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LegendaryRitualListener extends BukkitRunnable {

    private final Starborne plugin;
    private final LegendaryRitualManager ritualManager;

    public LegendaryRitualListener(
            Starborne plugin,
            LegendaryRitualManager ritualManager
    ) {
        this.plugin = plugin;
        this.ritualManager = ritualManager;
    }

    @Override
    public void run() {
        for (LegendaryRitualManager.Ritual ritual
                : ritualManager.getActiveRituals().values()) {

            UUID ritualId = ritual.getId();
            Location location = ritual.getLocation();
            World world = location.getWorld();

            if (world == null) {
                ritualManager.removeRitual(ritualId);
                continue;
            }

            long remaining = ritual.getSecondsRemaining();

            if (remaining <= 0) {
                ritualManager.removeRitual(ritualId);

                Bukkit.broadcastMessage(
                        ChatColor.LIGHT_PURPLE
                                + "✦ A Legendary ritual has finished at "
                                + ChatColor.WHITE
                                + world.getName() + " "
                                + location.getBlockX() + ", "
                                + location.getBlockY() + ", "
                                + location.getBlockZ()
                                + ChatColor.LIGHT_PURPLE + "!"
                );

                Bukkit.broadcastMessage(
                        ChatColor.GRAY
                                + "The ritual is complete, but its reward "
                                + "system is still being connected."
                );

                continue;
            }

            long totalDuration = Math.max(
                    1L,
                    plugin.getConfig().getLong(
                            "legendary.ritual-duration-seconds", 900L
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

            if (elapsed > 0 && elapsed % leakInterval < 1) {
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
