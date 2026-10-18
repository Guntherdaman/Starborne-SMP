
package com.starborne.souls;

import com.starborne.Starborne;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PvPTierListener implements Listener {

    private final Starborne plugin;

    public PvPTierListener(Starborne plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Only count deaths caused by another player.
        // Environmental deaths do not affect PvP tier progress.
        if (killer == null || killer.getUniqueId().equals(
                victim.getUniqueId())) {
            return;
        }

        PvPTierManager manager = plugin.getPvPTierManager();

        PvPTier oldKillerTier = manager.getTier(
                killer.getUniqueId()
        );
        PvPTier newKillerTier = manager.recordKill(
                killer.getUniqueId()
        );

        PvPTier oldVictimTier = manager.getTier(
                victim.getUniqueId()
        );
        PvPTier newVictimTier = manager.recordDeath(
                victim.getUniqueId()
        );

        killer.sendMessage(
                "§5✦ §dPvP Progress: §f"
                        + manager.getProgress(killer.getUniqueId())
                        + "/3"
        );

        victim.sendMessage(
                "§5✦ §dPvP Progress: §f"
                        + manager.getProgress(victim.getUniqueId())
                                               + "/3"
        );

        if (oldKillerTier != newKillerTier) {
            killer.sendMessage(
                    "§6✦ Your PvP tier increased to §e"
                            + newKillerTier.name() + "§6!"
            );
        }

        if (oldVictimTier != newVictimTier) {
            victim.sendMessage(
                    "§c✦ Your PvP tier decreased to §e"
                            + newVictimTier.name() + "§c!"
            );
        }
    }
}
