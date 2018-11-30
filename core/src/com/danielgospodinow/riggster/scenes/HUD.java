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

public class HUD {

    private Stage stage;
    private Viewport viewport;

    private Integer health;
    private Integer mana;
    private Integer heroDamage;

    private Label healthLabel;
    private Label manaLabel;
    private Label heroDamageLabel;

    public HUD(SpriteBatch spriteBatch) {
        this.health = 100;
        this.mana = 100;
        this.heroDamage = 5;

        this.viewport = new FitViewport(1200, 800, new OrthographicCamera());
        this.stage = new Stage(this.viewport, spriteBatch);

        Table table = new Table();
        table.bottom();
        table.left();
        table.setFillParent(true);

        this.healthLabel = new Label(String.format("HP: %d", this.health), new Label.LabelStyle(new BitmapFont(),
                Color.GREEN));
        this.manaLabel = new Label(String.format("MP: %d", this.mana), new Label.LabelStyle(new BitmapFont(),
                Color.BLUE));
        this.heroDamageLabel = new Label(String.format("Damage: %d", this.heroDamage),
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
}
