
package com.starborne.souls;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvPTierManager {

    private final Map<UUID, PvPTier> tiers =
            new HashMap<>();

    private final Map<UUID, Integer> progress =
            new HashMap<>();

    public PvPTier getTier(UUID playerId) {
        return tiers.getOrDefault(playerId, PvPTier.F);
    }

    public void setTier(UUID playerId, PvPTier tier) {
        if (playerId == null || tier == null) {
            throw new IllegalArgumentException(
                    "Player UUID and tier cannot be null."
            );
        }

        tiers.put(playerId, tier);
        progress.remove(playerId);
    }

    public int getProgress(UUID playerId) {
        return progress.getOrDefault(playerId, 0);
    }

    public PvPTier recordKill(UUID playerId) {
        return recordResult(playerId, 1);
    }

    public PvPTier recordDeath(UUID playerId) {
        return recordResult(playerId, -1);
    }

    private PvPTier recordResult(UUID playerId, int result) {
        if (playerId == null) {
            throw new IllegalArgumentException(
                    "Player UUID cannot be null."
            );
        }

        int newProgress = getProgress(playerId) + result;

        if (newProgress >= 3) {
            PvPTier newTier = getTier(playerId).getNextTier();
            tiers.put(playerId, newTier);
            progress.remove(playerId);
            return newTier;
        }

        if (newProgress <= -3) {
            PvPTier newTier = getTier(playerId).getPreviousTier();
            tiers.put(playerId, newTier);
            progress.remove(playerId);
            return newTier;
        }

        progress.put(playerId, newProgress);
        return getTier(playerId);
    }
}

