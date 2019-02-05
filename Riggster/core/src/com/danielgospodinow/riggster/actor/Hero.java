package com.danielgospodinow.riggster.actor;

import com.danielgospodinow.riggster.treasure.Spell;
import com.danielgospodinow.riggster.treasure.Weapon;

public abstract class Hero extends Character {

    private final int INITIAL_HEALTH;
    private final int INITIAL_MANA;

    public Hero(String name, int health, int mana) {
        super(name, health, mana);

        this.INITIAL_HEALTH = health;
        this.INITIAL_MANA = mana;
    }

    public void takeHealing(int healingPoints) {
        if(!this.isAlive() || healingPoints <= 0) {
            return;
        }

        int healing = (this.getHealth() + healingPoints >= INITIAL_HEALTH) ? INITIAL_HEALTH : this.getHealth() + healingPoints;
        this.setHealth(healing);
    }

    public void takeMana(int manaPoints) {
        if(!this.isAlive() || manaPoints <= 0) {
            return;
        }

        int mealing = (this.getMana() + manaPoints >= INITIAL_MANA) ? INITIAL_MANA : this.getMana() + manaPoints;
        this.setMana(mealing);
    }

    public void equip(Weapon weapon) {
        if(this.getWeapon() != null && weapon != null && this.getWeapon().getDamage() >= weapon.getDamage()) {
            return;
        }

        this.setWeapon(weapon);
    }

    public void learn(Spell spell) {
        if(this.getSpell() != null && spell != null && this.getSpell().getDamage() >= spell.getDamage()) {
            return;
        }

        this.setSpell(spell);
    }
}
