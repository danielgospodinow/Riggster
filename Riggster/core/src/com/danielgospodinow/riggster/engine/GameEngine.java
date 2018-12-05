package com.danielgospodinow.riggster.engine;

import com.danielgospodinow.riggster.actor.Enemy;
import com.danielgospodinow.riggster.actor.Hero;
import com.danielgospodinow.riggster.actor.Position;
import com.danielgospodinow.riggster.treasure.Treasure;

import java.util.Arrays;
import java.util.LinkedList;

public class GameEngine {

    private Map map;
    private Hero hero;
    private LinkedList<Enemy> enemies;
    private LinkedList<Treasure> treasures;

    public GameEngine(char[][] map, Hero hero, Enemy[] enemies, Treasure[] treasures) {
        this.map = new Map(map);
        this.hero = hero;
        this.enemies = new LinkedList<Enemy>(Arrays.asList(enemies));
        this.treasures = new LinkedList<Treasure>(Arrays.asList(treasures));
    }

    public String makeMove(Direction direction) {
        Position newPosition = this.map.getHeroPosition().clone();
        newPosition.addDirection(direction.getDirection());

        if(newPosition.row < 0 ||
                newPosition.col < 0 ||
                newPosition.row >= this.map.getRows() ||
                newPosition.col >= this.map.getCols()) {
            return "Invalid direction";
        }

        MapObjects objAtNewPosition = this.map.getObjectAt(newPosition);
        switch (objAtNewPosition) {
            case EMPTY_SPOT:
                this.map.moveHero(newPosition);
                return "You moved successfully to the next position.";
            case OBSTACLE:
                return "Wrong move. There is an obstacle and you cannot bypass it.";
            case TREASURE:
                this.map.moveHero(newPosition);
                return this.treasures.removeFirst().collect(this.hero);
            case ENEMY:
                boolean heroWin = this.duel(this.hero, this.enemies.removeFirst());
                if(heroWin) {
                    this.map.moveHero(newPosition);
                    return "Enemy died.";
                } else {
                    return "Hero is dead! Game over!";
                }
            case EXIT:
                return "You have successfully passed through the dungeon. Congrats!";
            default: return "Unknown command entered.";
        }
    }

    private boolean duel(Hero hero, Enemy enemy) {
        while(hero.isAlive() && enemy.isAlive()) {
            enemy.takeDamage(hero.attack());

            if(enemy.isAlive()) {
                hero.takeDamage(enemy.attack());
            }
        }

        return hero.isAlive();
    }
}
