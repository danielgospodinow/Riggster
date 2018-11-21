package com.danielgospodinow.riggster.treasure;

public abstract class PotionTreasure implements Treasure {

    private int restorePoints;

    public PotionTreasure(int restorePoints) {
        this.restorePoints = restorePoints;
    }

    public int heal() {
        return this.restorePoints;
    }
}
