package com.danielgospodinow.riggster.actor;

import com.danielgospodinow.riggster.treasure.Spell;
import com.danielgospodinow.riggster.treasure.Weapon;

public interface Actor {

    String getName();
    int getHealth();
    int getMana();
    boolean isAlive();
    void takeDamage(int damagePoints);
    Weapon getWeapon();
    Spell getSpell();
    int attack();
}
