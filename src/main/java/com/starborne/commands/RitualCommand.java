
package com.starborne.commands;

import com.starborne.Starborne;
import com.starborne.rituals.LegendaryRitualManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RitualCommand implements CommandExecutor {

    private final Starborne plugin;
    private final LegendaryRitualManager ritualManager;

    public RitualCommand(
            Starborne plugin,
            LegendaryRitualManager ritualManager
    ) {
        this.plugin = plugin;
        this.ritualManager = ritualManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used in-game.");
            return true;
        }

        if (!player.hasPermission("starborne.ritual.admin")) {
            player.sendMessage(
                    ChatColor.RED
                            + "You don't have permission to start test rituals."
            );
            return true;
        }

        if (!ritualManager.areRitualsEnabled()) {
            player.sendMessage(
                    ChatColor.RED
                            + "Legendary crafting and rituals are disabled!"
            );
            return true;
        }

        Location location = player.getLocation().getBlock().getLocation();

        if (!ritualManager.startRitual(location, player.getUniqueId())) {
            player.sendMessage(
                    ChatColor.RED
                            + "Couldn't start a ritual here. "
                            + "Check whether another ritual is active "
                            + "at this block."
            );
            return true;
        }

        player.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "✦ Test Legendary ritual started!"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "Location: "
                        + location.getBlockX() + ", "
                        + location.getBlockY() + ", "
                        + location.getBlockZ()
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "This is a test ritual; no item is awarded yet."
        );

        return true;
    }
}
