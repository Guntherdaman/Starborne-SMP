
package com.starborne.commands;

import com.starborne.Starborne;
import com.starborne.souls.SoulPowerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoulPowerCommand implements CommandExecutor {

    private final Starborne plugin;

    public SoulPowerCommand(Starborne plugin) {
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
            sender.sendMessage("This command can only be used in-game.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        SoulPowerManager manager = plugin.getSoulPowerManager();

        if (!manager.isPowerItem(item)) {
            player.sendMessage(
                    ChatColor.RED
                            + "Hold a registered Legendary or Mythic item "
                            + "in your main hand!"
            );
            return true;
        }

        String type = manager.getItemType(item);
        int current = manager.getSoulPower(item);
        int maximum = manager.getMaximumSoulPower(item);

        player.sendMessage(ChatColor.DARK_PURPLE + "✦ STARBORNE SOUL POWER ✦");
        player.sendMessage(
                ChatColor.GRAY + "Item type: "
                        + ChatColor.LIGHT_PURPLE + type
        );
        player.sendMessage(
                ChatColor.GRAY + "Soul Power: "
                        + ChatColor.LIGHT_PURPLE
                        + current + "/" + maximum
        );

        return true;
    }
}
