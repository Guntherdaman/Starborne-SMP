
package com.starborne.souls;

public enum PvPTier {

    S(75),
    A(50),
    B(25),
    C(25),
    D(15),
    F(15);

    private final int soulPower;

    PvPTier(int soulPower) {
        this.soulPower = soulPower;
    }

    public int getSoulPower() {
        return soulPower;
    }

    public PvPTier getNextTier() {
        return switch (this) {
            case S -> S;
            case A -> S;
            case B -> A;
            case C -> B;
            case D -> C;
            case F -> D;
        };
    }

    public PvPTier getPreviousTier() {
        return switch (this) {
            case S -> A;
            case A -> B;
            case B -> C;
            case C -> D;
            case D -> F;
            case F -> F;
        };
    }
}

