package com.danielgospodinow.riggster.treasure;

import com.danielgospodinow.riggster.actor.Hero;

public class Weapon extends FightTreasure {

    public Weapon(String name, int damage) {
        super(name, damage);
    }

    @Override
    public String collect(Hero hero) {
        hero.equip(this);
        return String.format("Weapon found! Damage points: %d", this.getDamage());
    }
}
