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
import com.danielgospodinow.riggster.screens.PlayScreen;

import java.awt.*;

public class Character extends Hero {

    public enum MoveDirection { RIGHT, LEFT, UP, DOWN }

    private Sprite sprite;
    private String spriteName;
    private Position position;
    private Label nameLabel;

    public Character(String sprite, String name, Position position) {
        super(name, 100, 100);

        this.spriteName = sprite;
        this.sprite = new Sprite(new Texture(Gdx.files.internal(this.spriteName + ".png")));
        this.nameLabel = new Label(this.getName(), new Label.LabelStyle(new BitmapFont(), Color.GREEN));

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
                if(this.position.row + 1 >= PlayScreen.getTilemapHeight() || isColliding(0, -1)) { return false; }

                this.setPosition(new Position(this.getPosition().row + 1, this.getPosition().col));
                return true;
            case RIGHT:
                if(this.position.col + 1 >= PlayScreen.getTilemapWidth() || isColliding(1, 0)) { return false; }

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
        for(Rectangle rect: PlayScreen.staticObjects) {
            if(player.intersects(rect)) {
                return true;
            }
        }

        return false;
    }
}
