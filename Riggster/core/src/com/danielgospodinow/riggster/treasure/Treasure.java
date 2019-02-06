package com.danielgospodinow.riggster.treasure;

import com.danielgospodinow.riggster.actor.Hero;
import com.danielgospodinow.riggster.utils.RandomNumberGenerator;

public abstract class Treasure {

    public static Treasure getRandomTreasure(int id) {
        int randomTreasureNumber = RandomNumberGenerator.getRandomInt(1, 4);

        Treasure treasure = null;
        switch (randomTreasureNumber) {
            case 1:
                treasure = new HealthPotion(RandomNumberGenerator.getRandomInt(5,50), id);
                break;
            case 2:
                treasure = new ManaPotion(RandomNumberGenerator.getRandomInt(1,40), id);
                break;
            case 3:
                int weaponDamage = RandomNumberGenerator.getRandomInt(2,40);
                treasure = new Weapon(String.format("%d damage weapon", weaponDamage), weaponDamage, id);
                break;
            case 4:
                int spellDamage = RandomNumberGenerator.getRandomInt(10,70);
                int spellCost = RandomNumberGenerator.getRandomInt(5,50);
                treasure = new Spell(String.format("%d damage spell", spellDamage), spellDamage, spellCost, id);
                break;
        }

        return treasure;
    }

    public abstract String collect(Hero hero);

    private boolean isCollected;
    protected int id;

    public Treasure(int id) {
        this.isCollected = false;
        this.id = id;
    }

    public boolean isCollected() {
        return this.isCollected;
    }

    public int getId() {
        return this.id;
    }

    protected void markAsCollected() {
        this.isCollected = true;
    }
}
