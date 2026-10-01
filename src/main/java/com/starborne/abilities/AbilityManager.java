package com.starborne.abilities;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityManager {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public boolean activate(Player player, Ability ability) {

        if (isOnCooldown(player, ability)) {
            long remaining = getRemainingCooldown(player, ability);
            long seconds = (remaining + 999) / 1000;

            player.sendMessage("§cThat ability is on cooldown for " + seconds + "s.");
            return false;
        }

        ability.activate(player);

        setCooldown(player, ability);

        return true;
    }

    public boolean isOnCooldown(Player player, Ability ability) {
        return getRemainingCooldown(player, ability) > 0;
    }

    public long getRemainingCooldown(Player player, Ability ability) {

        Map<String, Long> playerCooldowns =
                cooldowns.get(player.getUniqueId());

        if (playerCooldowns == null) {
            return 0;
        }

        Long endTime = playerCooldowns.get(ability.getName());

        if (endTime == null) {
            return 0;
        }

        return Math.max(0, endTime - System.currentTimeMillis());
    }

    private void setCooldown(Player player, Ability ability) {

        cooldowns
                .computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                .put(
                        ability.getName(),
                        System.currentTimeMillis() + ability.getCooldown()
                );
    }

    public void clearCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
