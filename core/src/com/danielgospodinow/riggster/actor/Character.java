package com.danielgospodinow.riggster.actor;

import com.danielgospodinow.riggster.treasure.Spell;
import com.danielgospodinow.riggster.treasure.Weapon;

public abstract class Character implements Actor {

    private Weapon weapon = null;
    private Spell spell = null;
    private String name;
    private int health;
    private int mana;

    public Character(String name, int health, int mana) {
        this.name = name;
        this.health = health;
        this.mana = mana;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getHealth() {
        return this.health;
    }

    @Override
    public int getMana() {
        return this.mana;
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    @Override
    public Weapon getWeapon() {
        return this.weapon;
    }

    @Override
    public Spell getSpell() {
        return this.spell;
    }

    @Override
    public void takeDamage(int damagePoints) {
        if(!this.isAlive()) {
            return;
        }

        this.health -= damagePoints;

        if(this.health <= 0) {
            this.health = 0;
        }
    }

    @Override
    public int attack() {
        if(this.weapon != null && this.spell != null) {
            if(spell.getManaCost() <= this.getMana()) {
                if(this.weapon.getDamage() > this.spell.getDamage()) {
                    return this.weapon.getDamage();
                } else {
                    this.mana -= spell.getManaCost();
                    return this.spell.getDamage();
                }
            } else {
                return this.weapon.getDamage();
            }
        } else if(this.weapon != null) {
            return this.weapon.getDamage();
        } else if(this.spell != null && spell.getManaCost() <= this.getMana()) {
            this.mana -= spell.getManaCost();
            return this.spell.getDamage();
        } else {
            return 0;
        }
    }

    protected void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    protected void setSpell(Spell spell) {
        this.spell = spell;
    }

    protected void setHealth(int health) {
        this.health = health;
    }

    protected void setMana(int mana) {
        this.mana = mana;
    }
}
