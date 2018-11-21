package com.danielgospodinow.riggster;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.net.InetSocketAddress;

public class Game extends ApplicationAdapter {

	private Client client;

	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private Vector3 position;
	
	@Override
	public void create () {
//		this.client = new Client(new InetSocketAddress("localhost", 8888));
//		new Thread(this.client).start();

		this.shapeRenderer = new ShapeRenderer();
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.position = new Vector3((float) Gdx.graphics.getWidth()/2, (float) Gdx.graphics.getHeight()/2, 0);
	}

	@Override
	public void render () {
//		for(String mess: this.client.getMessages()) {
//			System.out.println(String.format("We got: %s", mess));
//		}
//        this.client.getMessages().clear();

		this.camera.update();

		if(Gdx.input.isTouched()) {
			this.position.x = Gdx.input.getX();
			this.position.y = Gdx.graphics.getHeight() - Gdx.input.getY();
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		this.shapeRenderer.setColor(Color.BLACK);
		this.shapeRenderer.circle(this.position.x, this.position.y, 12);
		this.shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		this.shapeRenderer.dispose();
	}
}
