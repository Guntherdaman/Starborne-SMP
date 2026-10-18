
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
import org.bukkit.inventory.ItemStack;

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

        ItemStack item = slot.getItem(player);

        String abilityId =
                plugin.getAbilityItem().getAbilityId(item);

        if (abilityId == null) {
            player.sendMessage(
                    "§cNo Starborne ability is assigned to that item."
            );
            return true;
        }

        AbilityRegistry registry = plugin.getAbilityRegistry();
        Ability ability = registry.get(abilityId);

        if (ability == null) {
            player.sendMessage(
                    "§cThat item's ability is not registered."
            );
            return true;
        }

        if (ability.getSlot() != slot) {
            player.sendMessage(
                    "§cThat ability belongs to a different slot."
            );
            return true;
        }

        AbilityManager manager = plugin.getAbilityManager();
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
