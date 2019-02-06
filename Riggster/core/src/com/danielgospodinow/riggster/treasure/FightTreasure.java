package com.danielgospodinow.riggster.treasure;

public abstract class FightTreasure extends Treasure {

    private String name;
    private int damage;

    public FightTreasure(String name, int damage, int id) {
        super(id);
        this.name = name;
        this.damage = damage;
    }

    public String getName() {
        return this.name;
    }

    public int getDamage() {
        return this.damage;
    }
}
