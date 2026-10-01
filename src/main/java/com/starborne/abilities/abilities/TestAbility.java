package com.starborne.abilities.abilities;

import com.starborne.abilities.Ability;
import com.starborne.abilities.AbilitySlot;
import org.bukkit.entity.Player;

public class TestAbility implements Ability {

    @Override
    public String getName() {
        return "Test Ability";
    }

    @Override
    public AbilitySlot getSlot() {
        return AbilitySlot.MAIN_HAND;
    }

    @Override
    public long getCooldown() {
        return 5000;
    }

    @Override
    public void activate(Player player) {
        player.sendMessage("§d§l★ STARBORNE ★");
        player.sendMessage("§fYour ability system is working!");
    }
}
