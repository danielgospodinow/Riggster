package com.danielgospodinow.riggster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.danielgospodinow.riggster.actor.Hero;

public class Character extends Hero {

    public enum MoveDirection { RIGHT, LEFT, UP, DOWN };

    private Sprite sprite;

    public Character(String name, int health, int mana) {
        super(name, health, mana);

        this.sprite = new Sprite(new Texture(Gdx.files.internal("character4.png")));
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void move(MoveDirection direction) {
        switch (direction) {
            case UP:
                this.getSprite().setPosition(this.getSprite().getX(), this.getSprite().getY() + 16);
                break;
            case DOWN:
                this.getSprite().setPosition(this.getSprite().getX(), this.getSprite().getY() - 16);
                break;
            case RIGHT:
                this.getSprite().setPosition(this.getSprite().getX() + 16, this.getSprite().getY());
                break;
            case LEFT:
                this.getSprite().setPosition(this.getSprite().getX() - 16, this.getSprite().getY());
                break;
        }
    }
}
