
package com.starborne.trust;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TrustManager {

    private final Map<UUID, Set<UUID>> trustedPlayers =
            new HashMap<>();

    public boolean trust(UUID owner, UUID target) {
        if (owner.equals(target)) {
            return false;
        }

        return trustedPlayers
                .computeIfAbsent(
                        owner,
                        uuid -> new HashSet<>()
                )
                .add(target);
    }

    public boolean untrust(UUID owner, UUID target) {
        Set<UUID> trusted = trustedPlayers.get(owner);

        if (trusted == null) {
            return false;
        }

        boolean removed = trusted.remove(target);

        if (trusted.isEmpty()) {
            trustedPlayers.remove(owner);
        }

        return removed;
    }

    public boolean isTrusted(UUID owner, UUID target) {
        Set<UUID> trusted = trustedPlayers.get(owner);

        return trusted != null && trusted.contains(target);
    }

    public Set<UUID> getTrustedPlayers(UUID owner) {
        Set<UUID> trusted = trustedPlayers.get(owner);

        if (trusted == null) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(
                new HashSet<>(trusted)
        );
    }
}

