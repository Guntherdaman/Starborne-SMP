
package com.starborne.commands;

import com.starborne.Starborne;
import com.starborne.trust.TrustManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrustCommand implements CommandExecutor {

    private final Starborne plugin;

    public TrustCommand(Starborne plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                    "§cOnly players can use this command."
            );
            return true;
        }

        TrustManager trustManager = plugin.getTrustManager();

        if (args.length == 0) {
            player.sendMessage("§d§l★ STARBORNE TRUST ★");
            player.sendMessage("§f/trust <player> §7- Trust a player");
            player.sendMessage("§f/trust remove <player> §7- Remove trust");
            player.sendMessage("§f/trust list §7- List trusted players");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            Set<UUID> trusted =
                    trustManager.getTrustedPlayers(player.getUniqueId());

            if (trusted.isEmpty()) {
                player.sendMessage("§7You haven't trusted anyone yet.");
                return true;
            }

            String names = trusted.stream()
                    .map(Bukkit::getOfflinePlayer)
                    .map(offlinePlayer -> {
                        String name = offlinePlayer.getName();
                        return name != null ? name : "Unknown Player";
                    })
                    .collect(Collectors.joining("§7, §f"));

            player.sendMessage("§d§l★ YOUR TRUSTED PLAYERS ★");
            player.sendMessage("§f" + names);
            return true;
        }

        boolean removing =
                args[0].equalsIgnoreCase("remove");

        String targetName;

        if (removing) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /trust remove <player>");
                return true;
            }

            targetName = args[1];
        } else {
            targetName = args[0];
        }

        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            player.sendMessage(
                    "§cThat player must be online."
            );
            return true;
        }

        UUID ownerId = player.getUniqueId();
        UUID targetId = target.getUniqueId();

        if (ownerId.equals(targetId)) {
            player.sendMessage("§cYou can't trust or remove yourself.");
            return true;
        }

        if (removing) {
            if (trustManager.untrust(ownerId, targetId)) {
                player.sendMessage(
                        "§eYou no longer trust §f"
                                + target.getName() + "§e."
                );
            } else {
                player.sendMessage(
                        "§cThat player isn't on your trust list."
                );
            }
        } else {
            if (trustManager.trust(ownerId, targetId)) {
                player.sendMessage(
                        "§aYou now trust §f"
                                + target.getName() + "§a!"
                );
            } else {
                player.sendMessage(
                        "§eYou already trust that player."
                );
            }
        }

        return true;
    }
}

