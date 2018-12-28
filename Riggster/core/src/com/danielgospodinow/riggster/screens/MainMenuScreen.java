package com.danielgospodinow.riggster.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class MainMenuScreen implements Screen {

    private Game game;

    private Stage stage;

    private TextField nameInput;
    private TextButton connectButton;

    public MainMenuScreen(final com.danielgospodinow.riggster.Game game) {
        this.game = game;

        this.stage = new Stage();
        Gdx.input.setInputProcessor(this.stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        this.nameInput = new TextField("", skin);
        this.nameInput.setAlignment(Align.center);

        this.connectButton = new TextButton("Connect", skin);
        final MainMenuScreen that = this;
        this.connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!that.getTextfieldText().equals("")) {
                    game.setScreen(new PlayScreen(game, that.getTextfieldText()));
                }
            }
        });

        Table table = new Table();
        table.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()/ 2);
        table.add(new Label("Enter your name:", skin)).padBottom(20);
        table.row();
        table.add(this.nameInput).padBottom(20);
        table.row();
        table.add(this.connectButton);
        this.stage.addActor(table);

        Label logoLabel = new Label("Riggster", skin);
        logoLabel.setFontScale(2f);
        logoLabel.setWidth(logoLabel.getWidth() * 2);
        logoLabel.setPosition(Gdx.graphics.getWidth() / 2f - logoLabel.getWidth() / 2, Gdx.graphics.getHeight() / 1.2f);
        this.stage.addActor(logoLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.stage.draw();
        this.stage.act();
    }

    public String getTextfieldText() {
        return this.nameInput.getText();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
