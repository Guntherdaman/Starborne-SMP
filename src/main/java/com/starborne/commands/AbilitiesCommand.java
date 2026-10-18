
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

public class AbilitiesCommand implements CommandExecutor {

    private final Starborne plugin;

    public AbilitiesCommand(Starborne plugin) {
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

        player.sendMessage("§5§l★ STARBORNE ABILITIES ★");
        player.sendMessage("§7Your equipped ability slots:");

        AbilityRegistry registry = plugin.getAbilityRegistry();
        AbilityManager manager = plugin.getAbilityManager();

        for (AbilitySlot slot : AbilitySlot.values()) {
            ItemStack item = slot.getItem(player);

            String abilityId =
                    plugin.getAbilityItem().getAbilityId(item);

            String slotName = switch (slot) {
                case HELMET -> "Helmet";
                case CHESTPLATE -> "Chestplate";
                case LEGGINGS -> "Leggings";
                case BOOTS -> "Boots";
                case MAIN_HAND -> "Main Hand";
            };

            String commandName =
                    "/ability" + slot.getNumber();

            if (abilityId == null) {
                player.sendMessage(
                        "§d" + commandName
                                + " §8| §f" + slotName
                                + " §8| §7No ability assigned"
                );
                continue;
            }

            Ability ability = registry.get(abilityId);

            if (ability == null) {
                player.sendMessage(
                        "§d" + commandName
                                + " §8| §f" + slotName
                                + " §8| §cUnknown ability"
                );
                continue;
            }

            long remaining =
                    manager.getRemainingCooldown(player, ability);

            String status;

            if (remaining > 0) {
                long seconds = (remaining + 999) / 1000;
                status = "§cCooldown: " + seconds + "s";
            } else {
                status = "§aReady";
            }

            player.sendMessage(
                    "§d" + commandName
                            + " §8| §f" + slotName
                            + " §8| §b" + ability.getName()
                            + " §8| " + status
            );
        }

        return true;
    }
}

