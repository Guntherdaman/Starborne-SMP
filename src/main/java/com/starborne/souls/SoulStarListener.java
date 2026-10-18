
package com.starborne.souls;

import com.starborne.Starborne;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class SoulStarListener implements Listener {

    private final Starborne plugin;
    private final SoulStarItem soulStarItem;

    public SoulStarListener(Starborne plugin) {
        this.plugin = plugin;
        this.soulStarItem = new SoulStarItem(plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        PvPTier tier = plugin.getPvPTierManager()
                .getTier(victim.getUniqueId());

        ItemStack soulStar = soulStarItem.create(tier);

        // Add the Soul Star to the death drops.
        event.getDrops().add(soulStar);
    }
}
