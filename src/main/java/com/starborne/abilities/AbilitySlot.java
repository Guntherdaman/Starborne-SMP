
package com.starborne.abilities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum AbilitySlot {

    HELMET(1),
    CHESTPLATE(2),
    LEGGINGS(3),
    BOOTS(4),
    MAIN_HAND(5);

    private final int number;

    AbilitySlot(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public ItemStack getItem(Player player) {
        return switch (this) {
            case HELMET ->
                    player.getInventory().getHelmet();

            case CHESTPLATE ->
                    player.getInventory().getChestplate();

            case LEGGINGS ->
                    player.getInventory().getLeggings();

            case BOOTS ->
                    player.getInventory().getBoots();

            case MAIN_HAND ->
                    player.getInventory().getItemInMainHand();
        };
    }
}
