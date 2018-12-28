package com.danielgospodinow.riggster.treasure;

import com.danielgospodinow.riggster.actor.Hero;

public class Spell extends FightTreasure {

    private int manaCost;

    public Spell(String name, int damage, int manaCost) {
        super(name, damage);
        this.manaCost = manaCost;
    }

    public String collect(Hero hero) {
        this.markAsCollected();
        hero.learn(this);
        return String.format("Spell found! Damage points: %d, Mana cost: %d", this.getDamage(), this.getManaCost());
    }

    public int getManaCost() {
        return this.manaCost;
    }
}
