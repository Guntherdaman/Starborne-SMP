
package com.starborne.commands;

import com.starborne.Starborne;
import com.starborne.souls.PvPTier;
import com.starborne.souls.PvPTierManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PvPTierCommand implements CommandExecutor {

    private final Starborne plugin;

    public PvPTierCommand(Starborne plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!sender.hasPermission("starborne.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage("§cUsage: /pvptier <player> [tier]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage("§cThat player hasn't joined the server before.");
            return true;
        }

        PvPTierManager manager = plugin.getPvPTierManager();

        if (args.length == 1) {
            PvPTier currentTier = manager.getTier(target.getUniqueId());

            sender.sendMessage(
                    "§d" + target.getName()
                            + " §7has PvP tier §e" + currentTier.name()
                            + " §7(Soul Star value: §f"
                            + currentTier.getSoulPower() + "§7)."
            );
            return true;
        }

        PvPTier newTier;

        try {
            newTier = PvPTier.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException exception) {
            sender.sendMessage("§cInvalid tier! Choose S, A, B, C, D, or F.");
            return true;
        }

        manager.setTier(target.getUniqueId(), newTier);

        sender.sendMessage(
                "§aSet §d" + target.getName()
                        + "§a's PvP tier to §e" + newTier.name() + "§a."
        );

        if (target.isOnline()) {
            target.getPlayer().sendMessage(
                    "§5✦ An admin set your PvP tier to §d"
                            + newTier.name() + "§5."
            );
        }

        return true;
    }
}

