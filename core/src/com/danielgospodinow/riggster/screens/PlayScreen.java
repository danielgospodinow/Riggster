package com.danielgospodinow.riggster.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.danielgospodinow.riggster.Game;
import com.danielgospodinow.riggster.scenes.HUD;

public class PlayScreen implements Screen {

    private static final int WORLD_WIDTH = 800;
    private static final int WORLD_HEIGHT = 480;

    private Game game;

    private HUD hud;

    private OrthographicCamera camera;
    private Viewport cameraViewport;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int mapWidth;
    private int mapHeight;

    public PlayScreen(Game game) {
        this.game = game;

        loadHud();
        loadMap();
        loadCamera();
    }

    private void loadMap() {
        this.mapLoader = new TmxMapLoader();
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.textureMinFilter = Texture.TextureFilter.Linear;
        this.map = this.mapLoader.load("map.tmx", params);
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.map);

        this.mapHeight = (this.map.getProperties().get("height", Integer.class) * this.map.getProperties().get(
                "tileheight", Integer.class));
        this.mapWidth = (this.map.getProperties().get("width", Integer.class) * this.map.getProperties().get(
                "tilewidth", Integer.class));
    }

    private void loadHud() {
        this.hud = new HUD(this.game.spriteBatch);
    }

    private void loadCamera() {
        this.camera = new OrthographicCamera();
        this.cameraViewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, this.camera);

        Vector2 cameraStartPoint = new Vector2(this.cameraViewport.getWorldWidth() / 2,
                this.cameraViewport.getWorldHeight() / 2 + (float) this.mapHeight / 2 - (this.cameraViewport.getWorldHeight() - (float) this.mapHeight / 2));
        this.camera.position.set(cameraStartPoint, 0);
    }

    @Override
    public void render(float delta) {
        this.update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.mapRenderer.render();

        //?
        this.game.spriteBatch.setProjectionMatrix(this.hud.getStage().getCamera().combined);
        this.hud.getStage().draw();

    }

    public void update(float deltaTime) {
        handleInput(deltaTime);
        this.camera.update();
        this.mapRenderer.setView(this.camera);
    }

    private void handleInput(float deltaTime) {
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            this.camera.position.y += 150 * deltaTime;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            this.camera.position.y -= 150 * deltaTime;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.camera.position.x += 150 * deltaTime;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.camera.position.x -= 150 * deltaTime;
        }
    }

    @Override
    public void resize(int width, int height) {
        this.cameraViewport.update(width, height);
    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
