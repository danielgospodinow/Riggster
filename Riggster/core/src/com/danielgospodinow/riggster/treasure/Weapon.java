package com.danielgospodinow.riggster.treasure;

import com.danielgospodinow.riggster.actor.Hero;

public class Weapon extends FightTreasure {

    public Weapon(String name, int damage, int id) {
        super(name, damage, id);
    }

    @Override
    public String collect(Hero hero) {
        this.markAsCollected();
        hero.equip(this);
        return String.format("Weapon found! Damage points: %d", this.getDamage());
    }
}
