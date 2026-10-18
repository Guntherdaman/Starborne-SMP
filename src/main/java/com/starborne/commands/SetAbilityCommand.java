
package com.starborne.commands;

import com.starborne.Starborne;
import com.starborne.abilities.Ability;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetAbilityCommand implements CommandExecutor {

    private final Starborne plugin;

    public SetAbilityCommand(Starborne plugin) {
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
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("starborne.admin")) {
            player.sendMessage("§cYou don't have permission to do that.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§eUsage: /setability <ability_id>");
            return true;
        }

        String abilityId = args[0].toLowerCase();

        Ability ability = plugin.getAbilityRegistry().get(abilityId);

        if (ability == null) {
            player.sendMessage("§cThat ability ID is not registered.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            player.sendMessage("§cHold an item in your main hand first.");
            return true;
        }

        plugin.getAbilityItem().setAbilityId(item, abilityId);

        player.sendMessage(
                "§dAssigned ability §f" + abilityId
                        + " §dto your held item!"
        );

        return true;
    }
}

