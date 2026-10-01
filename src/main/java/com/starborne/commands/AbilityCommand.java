package com.starborne.commands;

import com.starborne.Starborne;
import com.starborne.abilities.Ability;
import com.starborne.abilities.AbilityManager;
import com.starborne.abilities.AbilityRegistry;
import com.starborne.abilities.AbilitySlot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbilityCommand implements CommandExecutor {

    private final Starborne plugin;

    public AbilityCommand(Starborne plugin) {
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
            sender.sendMessage("§cOnly players can use abilities.");
            return true;
        }

        AbilitySlot slot = getSlot(command.getName());

        if (slot == null) {
            player.sendMessage("§cUnknown ability slot.");
            return true;
        }

        AbilityRegistry registry = plugin.getAbilityRegistry();
        AbilityManager manager = plugin.getAbilityManager();

        Ability ability = registry.get("test ability");

        if (ability == null) {
            player.sendMessage("§cNo ability is registered for this slot yet.");
            return true;
        }

        if (ability.getSlot() != slot) {
            player.sendMessage("§cYou don't have an ability for this slot yet.");
            return true;
        }

        manager.activate(player, ability);

        return true;
    }

    private AbilitySlot getSlot(String commandName) {

        return switch (commandName.toLowerCase()) {
            case "ability1" -> AbilitySlot.HELMET;
            case "ability2" -> AbilitySlot.CHESTPLATE;
            case "ability3" -> AbilitySlot.LEGGINGS;
            case "ability4" -> AbilitySlot.BOOTS;
            case "ability5" -> AbilitySlot.MAIN_HAND;
            default -> null;
        };
    }
}
