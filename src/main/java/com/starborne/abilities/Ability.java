
package com.starborne.abilities;

import org.bukkit.entity.Player;

public interface Ability {

    String getId();

    String getName();

    AbilitySlot getSlot();

    long getCooldown();

    void activate(Player player);
}
