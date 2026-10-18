
package com.starborne.commands;

import com.starborne.Starborne;
import com.starborne.souls.PvPTier;
import com.starborne.souls.PvPTierManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoulsCommand implements CommandExecutor {

    private final Starborne plugin;

    public SoulsCommand(Starborne plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        OfflinePlayer target;

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                        "§cConsole usage: /souls <player>"
                );
                return true;
            }

            target = player;
        } else {
            target = Bukkit.getOfflinePlayer(args[0]);
        }

        PvPTierManager manager = plugin.getPvPTierManager();
        PvPTier tier = manager.getTier(target.getUniqueId());

        String playerName = target.getName();

        if (playerName == null) {
            playerName = args.length > 0
                    ? args[0]
                    : "Unknown Player";
        }

        sender.sendMessage("§5§l✦ STARBORNE SOULS ✦");
        sender.sendMessage("§fPlayer: §d" + playerName);
        sender.sendMessage("§fPvP Tier: §d" + tier.name());
        sender.sendMessage(
                "§fSoul Star Value: §d"
                        + tier.getSoulPower()
                        + " Soul Power"
        );

        int progress = manager.getProgress(target.getUniqueId());

        if (progress > 0) {
            sender.sendMessage(
                    "§7Progress toward promotion: §a"
                            + progress + "/3 kills"
            );
        } else if (progress < 0) {
            sender.sendMessage(
                    "§7Progress toward demotion: §c"
                            + (-progress) + "/3 deaths"
            );
        } else {
            sender.sendMessage("§7Tier progress: §f0/3");
        }

        return true;
    }
}

