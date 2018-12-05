package com.danielgospodinow.riggster.actor;

import com.danielgospodinow.riggster.treasure.Spell;
import com.danielgospodinow.riggster.treasure.Weapon;

public class Enemy extends Character {

    public Enemy(String name, int health, int mana, Weapon weapon, Spell spell) {
        super(name, health, mana);
        this.setWeapon(weapon);
        this.setSpell(spell);
    }
}
