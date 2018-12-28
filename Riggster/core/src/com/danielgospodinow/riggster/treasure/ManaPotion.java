package com.danielgospodinow.riggster.treasure;

import com.danielgospodinow.riggster.actor.Hero;

public class ManaPotion extends PotionTreasure {

    public ManaPotion(int manaPoints) {
        super(manaPoints);
    }

    @Override
    public String collect(Hero hero) {
        this.markAsCollected();
        hero.takeMana(this.heal());
        return String.format("Mana potion found! %d mana points added to your hero!", this.heal());
    }
}
