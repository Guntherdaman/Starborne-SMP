
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class StarborneCraftingTableListener implements Listener {

    private final Starborne plugin;
    private final StarborneCraftingTable craftingTable;
    private final StarborneCraftingTableManager tableManager;
    private final LegendaryRecipeMenu recipeMenu;

    public StarborneCraftingTableListener(
            Starborne plugin,
            StarborneCraftingTable craftingTable,
            StarborneCraftingTableManager tableManager,
            LegendaryRecipeMenu recipeMenu
    ) {
        this.plugin = plugin;
        this.craftingTable = craftingTable;
        this.tableManager = tableManager;
        this.recipeMenu = recipeMenu;
    }

    @EventHandler
    public void onTablePlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (!craftingTable.isCustomTable(item)) {
            return;
        }

        Block block = event.getBlockPlaced();

        if (block.getType() != Material.CRAFTING_TABLE) {
            return;
        }

        tableManager.registerTable(block.getLocation());

        event.getPlayer().sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "✦ Starborne Crafting Table placed!"
        );
    }

    @EventHandler
    public void onTableBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        if (!tableManager.isRegisteredTable(location)) {
            return;
        }

        tableManager.unregisterTable(location);

        event.getPlayer().sendMessage(
                ChatColor.GRAY
                        + "Starborne Crafting Table removed."
        );
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null
                || block.getType() != Material.CRAFTING_TABLE
                || !tableManager.isRegisteredTable(block.getLocation())) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        if (!plugin.getConfig().getBoolean(
                "legendary.crafting-enabled", true)) {
            player.sendMessage(
                    ChatColor.RED
                            + "Legendary crafting is currently disabled."
            );
            return;
        }

        // Pass the exact table location into the recipe menu.
        recipeMenu.open(player, block.getLocation());
    }
}
