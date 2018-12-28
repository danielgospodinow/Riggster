package com.danielgospodinow.riggster.server.gameobjects;

public class Treasure {

    private int x;
    private int y;

    public Treasure(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("%d %d", this.x, this.y);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Treasure))return false;
        Treasure otherTreasure = (Treasure)other;

        return (otherTreasure.x == this.x && otherTreasure.y == this.y);
    }
}
