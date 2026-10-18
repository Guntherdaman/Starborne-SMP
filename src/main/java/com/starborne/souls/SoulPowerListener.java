
package com.starborne.souls;

import com.starborne.Starborne;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class SoulPowerListener implements Listener {

    private final SoulPowerManager soulPowerManager;
    private final SoulStarItem soulStarItem;

    public SoulPowerListener(Starborne plugin) {
        this.soulPowerManager = plugin.getSoulPowerManager();
        this.soulStarItem = new SoulStarItem(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack soulStar = player.getInventory().getItemInMainHand();
        ItemStack target = player.getInventory().getItemInOffHand();

        if (!soulStarItem.isSoulStar(soulStar)
                || !soulPowerManager.isPowerItem(target)) {
            return;
        }

        event.setCancelled(true);

        int addedPower = soulStarItem.getSoulPower(soulStar);
        int currentPower = soulPowerManager.getSoulPower(target);
        int maximumPower = soulPowerManager.getMaximumSoulPower(target);

        if (addedPower <= 0) {
            player.sendMessage(
                    ChatColor.RED + "This Soul Star has no valid Soul Power."
            );
            return;
        }

        if (addedPower > maximumPower - currentPower) {
            player.sendMessage(
                    ChatColor.RED + "This upgrade would exceed the item's "
                            + maximumPower + " Soul Power maximum!"
            );
            player.sendMessage(
                    ChatColor.GRAY + "Current Soul Power: "
                            + currentPower + "/" + maximumPower
            );
            return;
        }

        if (!soulPowerManager.applySoulStar(
                target, soulStar, soulStarItem)) {
            player.sendMessage(
                    ChatColor.RED + "The Soul Power upgrade failed."
            );
            return;
        }

        // Consume exactly one Soul Star after a successful upgrade.
        if (soulStar.getAmount() > 1) {
            soulStar.setAmount(soulStar.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        player.sendMessage(
                ChatColor.LIGHT_PURPLE + "✦ Soul Power upgraded! "
                        + ChatColor.WHITE + currentPower + " → "
                        + (currentPower + addedPower) + "/"
                        + maximumPower
        );

        player.sendMessage(
                ChatColor.GRAY + "The Soul Star was consumed."
        );
    }
}
