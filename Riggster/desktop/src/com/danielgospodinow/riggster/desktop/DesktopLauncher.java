package com.danielgospodinow.riggster.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.danielgospodinow.riggster.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Riggster";
		config.width = 1200;
		config.height = 800;
		new LwjglApplication(new Game(), config);
	}
}
