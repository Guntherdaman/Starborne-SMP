
package com.starborne.rituals;

import com.starborne.Starborne;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class StarborneCraftingTableListener implements Listener {

    private final Starborne plugin;
    private final StarborneCraftingTable craftingTable;

    public StarborneCraftingTableListener(
            Starborne plugin,
            StarborneCraftingTable craftingTable
    ) {
        this.plugin = plugin;
        this.craftingTable = craftingTable;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.CRAFTING_TABLE) {
            return;
        }

        Player player = event.getPlayer();
        Location location = block.getLocation();

        // A normal crafting table must not trigger Starborne behavior.
        // The persistent item marker will be used when placing custom tables.
        if (!craftingTable.isCustomTable(
                player.getInventory().getItemInMainHand())) {
            return;
        }

        event.setCancelled(true);

        if (!plugin.getConfig().getBoolean(
                "legendary.crafting-enabled", true)) {
            player.sendMessage(
                    ChatColor.RED + "Legendary crafting is currently disabled."
            );
            return;
        }

        player.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "✦ Starborne Crafting Table detected!"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "The Legendary recipe menu is coming next."
        );
    }
}

