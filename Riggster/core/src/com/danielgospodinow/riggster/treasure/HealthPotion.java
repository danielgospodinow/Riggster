package com.danielgospodinow.riggster.treasure;

import com.danielgospodinow.riggster.actor.Hero;

public class HealthPotion extends PotionTreasure {

    public HealthPotion(int healingPoints) {
        super(healingPoints);
    }

    @Override
    public String collect(Hero hero) {
        this.markAsCollected();
        hero.takeHealing(this.heal());
        return String.format("Health potion found! %d health points added to your hero!", this.heal());
    }
}
