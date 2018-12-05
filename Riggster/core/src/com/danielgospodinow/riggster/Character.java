package com.danielgospodinow.riggster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.danielgospodinow.riggster.actor.Hero;
import com.danielgospodinow.riggster.actor.Position;
import com.danielgospodinow.riggster.screens.PlayScreen;

public class Character extends Hero {

    public enum MoveDirection { RIGHT, LEFT, UP, DOWN }

    private Sprite sprite;
    private Position position;

    public Character(String name, int health, int mana) {
        super(name, health, mana);

        this.sprite = new Sprite(new Texture(Gdx.files.internal("character4.png")));
        this.position = new Position(0,0);
    }

    public Character(String name, int health, int mana, Position position) {
        super(name, health, mana);

        this.sprite = new Sprite(new Texture(Gdx.files.internal("character3.png")));
        this.position = position;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
        this.sprite.setX(newPosition.col * this.sprite.getWidth());
        this.sprite.setY(PlayScreen.getMapHeight() - (1 + newPosition.row) * this.sprite.getHeight());
    }

    public boolean move(MoveDirection direction) {
        switch (direction) {
            case UP:
                if(this.position.row - 1 < 0) { return false; }

                this.getSprite().setPosition(this.getSprite().getX(), this.getSprite().getY() + (int) this.sprite.getWidth());
                this.position.row -= 1;
                return true;
            case DOWN:
                if(this.position.row + 1 >= PlayScreen.getTilemapHeight()) { return false; }

                this.getSprite().setPosition(this.getSprite().getX(), this.getSprite().getY() - (int) this.sprite.getWidth());
                this.position.row += 1;
                return true;
            case RIGHT:
                if(this.position.col + 1 >= PlayScreen.getTilemapWidth()) { return false; }

                this.getSprite().setPosition(this.getSprite().getX() + (int) this.sprite.getWidth(), this.getSprite().getY());
                this.position.col += 1;
                return true;
            case LEFT:
                if(this.position.col - 1 < 0) { return false; }

                this.getSprite().setPosition(this.getSprite().getX() - (int) this.sprite.getWidth(), this.getSprite().getY());
                this.position.col -= 1;
                return true;
            default: return false;
        }
    }
}
