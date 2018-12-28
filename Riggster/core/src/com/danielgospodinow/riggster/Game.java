package com.danielgospodinow.riggster;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.danielgospodinow.riggster.screens.MainMenuScreen;

public class Game extends com.badlogic.gdx.Game {

    public SpriteBatch spriteBatch;

    @Override
	public void create () {
        this.spriteBatch = new SpriteBatch();
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
        super.render();
	}
	
	@Override
	public void dispose () {
        super.dispose();
	}
}
