package com.starborne.abilities;

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
}
