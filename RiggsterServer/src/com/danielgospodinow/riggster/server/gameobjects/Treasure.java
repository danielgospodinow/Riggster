package com.danielgospodinow.riggster.server.gameobjects;

public class Treasure {

    private int id;

    public Treasure(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%d", this.id);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Treasure))return false;
        Treasure otherTreasure = (Treasure)other;

        return (otherTreasure.id == this.id);
    }
}
