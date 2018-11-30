package com.danielgospodinow.riggster;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.danielgospodinow.riggster.screens.PlayScreen;

public class Game extends com.badlogic.gdx.Game {

    public SpriteBatch spriteBatch;

    @Override
	public void create () {
        this.spriteBatch = new SpriteBatch();
        this.setScreen(new PlayScreen(this));
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
