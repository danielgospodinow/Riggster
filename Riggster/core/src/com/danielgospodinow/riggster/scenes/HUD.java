package com.danielgospodinow.riggster.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.danielgospodinow.riggster.actor.Character;

public class HUD {

    private Stage stage;
    private Viewport viewport;

    private Character character;
    private int characterHealth;
    private int characterMana;
    private int characterDamage;

    private Label healthLabel;
    private Label manaLabel;
    private Label heroDamageLabel;

    public HUD(SpriteBatch spriteBatch, Character character) {
        this.viewport = new FitViewport(1200, 800, new OrthographicCamera());
        this.stage = new Stage(this.viewport, spriteBatch);

        this.character = character;

        Table table = new Table();
        table.bottom();
        table.left();
        table.setFillParent(true);

        this.characterHealth = this.character.getHealth();
        this.characterMana = this.character.getMana();
        this.characterDamage = this.character.getDamage();

        this.healthLabel = new Label(String.format("HP: %d", this.characterHealth),
                new Label.LabelStyle(new BitmapFont(),
                        Color.GREEN));
        this.manaLabel = new Label(String.format("MP: %d", this.characterMana),
                new Label.LabelStyle(new BitmapFont(),
                        Color.BLUE));
        this.heroDamageLabel = new Label(String.format("Damage: %d", this.characterDamage),
                new Label.LabelStyle(new BitmapFont(), Color.RED));

        table.add(this.healthLabel).pad(2).fillY().align(Align.left);
        table.row();
        table.add(this.manaLabel).pad(2).fillY().align(Align.left);
        table.row();
        table.add(this.heroDamageLabel).pad(2).fillY().align(Align.left);

        this.stage.addActor(table);
    }

    public Stage getStage() {
        return this.stage;
    }

    public void update() {
        if (this.character.getHealth() != this.characterHealth || this.character.getMana() != this.characterMana || this.character.getDamage() != this.characterDamage) {
            this.characterHealth = this.character.getHealth();
            this.characterMana = this.character.getMana();
            this.characterDamage = this.character.getDamage();

            this.healthLabel.setText(String.format("HP: %d", this.character.getHealth()));
            this.manaLabel.setText(String.format("MP: %d", this.character.getMana()));
            this.heroDamageLabel.setText(String.format("Damage: %d", this.character.getDamage()));
        }
    }
}
