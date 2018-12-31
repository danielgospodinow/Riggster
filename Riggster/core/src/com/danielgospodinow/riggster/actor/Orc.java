package com.danielgospodinow.riggster.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.danielgospodinow.riggster.networking.NetworkOperator;
import com.danielgospodinow.riggster.screens.PlayScreen;
import com.danielgospodinow.riggster.treasure.Weapon;

public class Orc extends Enemy {

    private static String spriteName = "enemyOrc";
    private static String enemyName = "Orc";

    private Position position;
    private Sprite sprite;
    private Label nameLabel;
    private Label healthLabel;
    private Label damageLabel;

    private static final int ATTACK_DISTANCE = 5;
    private float moveTimer = 0;
    private float moveTime = 1f;

    private boolean isChasing = false;

    private Player character;
    private NetworkOperator networkOperator;

    private boolean currentlyInUseByNetwork = false;

    public Orc(String name, int row, int col, int health, int damage, Player character, NetworkOperator networkOperator) {
        super(name, health, 0, new Weapon("orc weapon", damage), null);
        this.sprite = new Sprite(new Texture(Gdx.files.internal(spriteName + ".png")));

        this.nameLabel = new Label(enemyName, new Label.LabelStyle(new BitmapFont(), Color.RED));
        this.healthLabel = new Label("HP " + String.valueOf(this.getHealth()), new Label.LabelStyle(new BitmapFont(), Color.GREEN));
        this.damageLabel = new Label("DP " + String.valueOf(this.getDamage()), new Label.LabelStyle(new BitmapFont(), Color.RED));

        this.setPosition(new Position(row, col));

        this.character = character;
        this.networkOperator = networkOperator;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
        this.sprite.setX(newPosition.col * this.sprite.getWidth());
        this.sprite.setY(PlayScreen.getMapHeight() - (1 + newPosition.row) * this.sprite.getHeight());
        this.nameLabel.setPosition(this.getSprite().getX() - this.nameLabel.getWidth() / 2 + this.sprite.getWidth() / 2,
                this.getSprite().getY() + 3 * this.sprite.getHeight());
        this.healthLabel.setPosition(this.getSprite().getX() - this.healthLabel.getWidth() / 2 + this.sprite.getWidth() / 2,
                this.getSprite().getY() + 2 * this.sprite.getHeight());
        this.damageLabel.setPosition(this.getSprite().getX() - this.damageLabel.getWidth() / 2 + this.sprite.getWidth() / 2,
                this.getSprite().getY() + 1 * this.sprite.getHeight());

    }

    public Position getPosition() {
        return this.position;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void updateInformation(int row, int col, int health, boolean currentlyInUse) {
        this.setPosition(new Position(row, col));
        this.setHealth(health);
        this.updateHealthLabel();
        this.currentlyInUseByNetwork = currentlyInUse;
    }

    public void draw(Batch batch) {
        this.sprite.draw(batch);
        this.nameLabel.draw(batch, 1);
        this.healthLabel.draw(batch, 1);
        this.damageLabel.draw(batch, 1);
    }

    public void update(float delta) {
        if(!character.isAlive() || this.currentlyInUseByNetwork) {
            return;
        }

        if(this.character.getPosition().row == this.getPosition().row && this.character.getPosition().col == this.getPosition().col) {
            duel();

            if(this.isAlive()) {
                this.networkOperator.updateEnemyStatus(this, false);
                this.isChasing = false;
            } else {
                //TODO: send that the enemy died and to be removed from every client
                this.networkOperator.removeEnemy(this);
            }
        } else {
            int rowDiff = Math.abs(this.character.getPosition().row - this.getPosition().row);
            int colDiff = Math.abs(this.character.getPosition().col - this.getPosition().col);

            if(rowDiff <= ATTACK_DISTANCE && colDiff <= ATTACK_DISTANCE) {
                if(!this.isChasing) {
                    this.isChasing = true;
                }

                if(this.moveTimer > this.moveTime) {
                    // Can chase character
                    Position newPos = this.getPosition();

                    if(rowDiff > colDiff) {
                        // Move one row closer
                        if(this.character.getPosition().row - this.getPosition().row < 0) {
                            // Move up
                            newPos.row -= 1;
                            this.setPosition(newPos);
                        } else {
                            // Move down
                            newPos.row += 1;
                            this.setPosition(newPos);
                        }
                    } else {
                        // Move one col closer
                        if(this.character.getPosition().col - this.getPosition().col < 0) {
                            // Move left
                            newPos.col-= 1;
                            this.setPosition(newPos);
                        } else {
                            // Move right
                            newPos.col += 1;
                            this.setPosition(newPos);
                        }
                    }

                    // Send new location to the server
                    this.networkOperator.updateEnemyStatus(this, true);
                    this.moveTimer = 0;
                } else {
                    // Should wait
                    this.moveTimer += 1 * delta;
                }
            } else {
                if(this.isChasing) {
                    this.isChasing = false;
                    // Send that you've lost ownership of the enemy
                    this.networkOperator.updateEnemyStatus(this, false);
                }

                if(this.moveTimer <= this.moveTime) {
                    this.moveTimer += 1 * delta;
                }
            }
        }
    }

    @Override
    public void takeDamage(int damagePoints) {
        super.takeDamage(damagePoints);
        this.updateHealthLabel();
    }

    private void updateHealthLabel() {
        this.healthLabel = new Label("HP " + String.valueOf(this.getHealth()), new Label.LabelStyle(new BitmapFont(), Color.GREEN));
        this.healthLabel.setPosition(this.getSprite().getX() - this.healthLabel.getWidth() / 2 + this.sprite.getWidth() / 2, this.getSprite().getY() + 2 * this.sprite.getHeight());
    }

    private void duel() {
        while(this.character.isAlive() && this.isAlive()) {
            this.takeDamage(this.character.attack());

            if(this.isAlive()) {
                this.character.takeDamage(this.attack());
            }
        }
    }
}
