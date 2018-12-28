package com.danielgospodinow.riggster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.danielgospodinow.riggster.actor.Hero;
import com.danielgospodinow.riggster.actor.Position;
import com.danielgospodinow.riggster.networking.NetworkOperations;
import com.danielgospodinow.riggster.networking.NetworkOperator;
import com.danielgospodinow.riggster.screens.PlayScreen;
import com.danielgospodinow.riggster.treasure.HealthPotion;
import com.danielgospodinow.riggster.treasure.Treasure;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Character extends Hero {

    public enum MoveDirection { RIGHT, LEFT, UP, DOWN }

    private static int CHARACTER_HEALTH = 100;
    private static int CHARACTER_MANA = 100;

    private static int TOTAL_ROWS;
    private static int TOTAL_COLS;

    private Sprite sprite;
    private String spriteName;
    private Position position;
    private Label nameLabel;

    private List<Rectangle> staticObjects;
    private HashMap<Rectangle, Treasure> treasureObjects;

    public Character(String sprite, String name, Position position, List<Rectangle> staticObjects,
                     HashMap<Rectangle, Treasure> treasureObjects) {
        super(name, CHARACTER_HEALTH, CHARACTER_MANA);

        this.spriteName = sprite;
        this.sprite = new Sprite(new Texture(Gdx.files.internal(this.spriteName + ".png")));
//        this.sprite.scale(0.5f);
        this.nameLabel = new Label(this.getName(), new Label.LabelStyle(new BitmapFont(), Color.GREEN));

        TOTAL_ROWS = PlayScreen.getMapHeight() / (int) this.sprite.getHeight();
        TOTAL_COLS = PlayScreen.getMapWidth() / (int) this.sprite.getWidth();


        this.staticObjects = staticObjects;
        this.treasureObjects = treasureObjects;

        this.setPosition(position);
    }

    public Character(String sprite, String name, Position position) {
        super(name, CHARACTER_HEALTH, CHARACTER_MANA);

        this.spriteName = sprite;
        this.sprite = new Sprite(new Texture(Gdx.files.internal(this.spriteName + ".png")));
//        this.sprite.scale(0.5f);
        this.nameLabel = new Label(this.getName(), new Label.LabelStyle(new BitmapFont(), Color.GREEN));

        TOTAL_ROWS = PlayScreen.getMapHeight() / (int) this.sprite.getHeight();
        TOTAL_COLS = PlayScreen.getMapWidth() / (int) this.sprite.getWidth();

        this.setPosition(position);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public String getSpriteName() {
        return this.spriteName;
    }

    public void draw(Batch batch) {
        this.sprite.draw(batch);
        this.nameLabel.draw(batch, 1);
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
        this.sprite.setX(newPosition.col * this.sprite.getWidth());
        this.sprite.setY(PlayScreen.getMapHeight() - (1 + newPosition.row) * this.sprite.getHeight());
        this.nameLabel.setPosition(this.getSprite().getX() - this.nameLabel.getWidth() / 2 + this.sprite.getWidth() / 2,
                this.getSprite().getY() + this.sprite.getHeight());
    }

    public boolean move(MoveDirection direction) {
        switch (direction) {
            case UP:
                if(this.position.row - 1 < 0 || isColliding(0, 1)) { return false; }

                this.setPosition(new Position(this.getPosition().row - 1, this.getPosition().col));
                return true;
            case DOWN:
                if(this.position.row + 1 >= TOTAL_ROWS|| isColliding(0, -1)) { return false; }

                this.setPosition(new Position(this.getPosition().row + 1, this.getPosition().col));
                return true;
            case RIGHT:
                if(this.position.col + 1 >= TOTAL_COLS || isColliding(1, 0)) { return false; }

                this.setPosition(new Position(this.getPosition().row, this.getPosition().col + 1));
                return true;
            case LEFT:
                if(this.position.col - 1 < 0 || isColliding(-1, 0)) { return false; }

                this.setPosition(new Position(this.getPosition().row, this.getPosition().col - 1));
                return true;
            default: return false;
        }
    }

    private boolean isColliding(int xChange, int yChange) {
        int spriteSize = (int) this.sprite.getWidth();
        Rectangle player = new Rectangle((int) this.sprite.getX() + xChange * spriteSize,
                (int) this.sprite.getY() + yChange * spriteSize,
                (int) this.sprite.getWidth(), (int) this.sprite.getHeight());

        // Check static objects collision
        for(Rectangle rect: this.staticObjects) {
            if(player.intersects(rect)) {
                return true;
            }
        }

        // Check treasures collision
        for(Rectangle rectangle: this.treasureObjects.keySet()) {
            if(player.intersects(rectangle)) {
                System.out.println("Yo, found a treasure!");

                Treasure currentTreasure = this.treasureObjects.get(rectangle);
                System.out.println(currentTreasure.collect(this) + "\n");
            }
        }

        return false;
    }
}
