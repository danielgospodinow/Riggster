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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.danielgospodinow.riggster.Character;
import com.danielgospodinow.riggster.Game;
import com.danielgospodinow.riggster.scenes.HUD;

public class PlayScreen implements Screen {

    private static final int WORLD_WIDTH = 1200;
    private static final int WORLD_HEIGHT = 800;

    private Game game;

    private HUD hud;

    private OrthographicCamera camera;
    private Viewport cameraViewport;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int mapWidth;
    private int mapHeight;

    private Character character;

    public PlayScreen(Game game) {
        this.game = game;

        loadMap();
        loadCamera();
        loadCharacter();
        loadHud();
    }

    private void loadCharacter() {
        this.character = new Character("Character", 100, 100);
        this.character.getSprite().setPosition(0, this.mapHeight - this.character.getSprite().getHeight());
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

        this.game.spriteBatch.setProjectionMatrix(this.hud.getStage().getCamera().combined);
        this.hud.getStage().draw();

        this.game.spriteBatch.begin();
        this.character.getSprite().draw(this.game.spriteBatch);
        this.game.spriteBatch.end();

    }

    public void update(float deltaTime) {
        handleInput(deltaTime);
        updateCamera();

        this.camera.update();
        this.mapRenderer.setView(this.camera);
    }

    private void handleInput(float deltaTime) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            this.character.move(Character.MoveDirection.UP);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            this.character.move(Character.MoveDirection.DOWN);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.D)) {

            this.character.move(Character.MoveDirection.RIGHT);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            this.character.move(Character.MoveDirection.LEFT);
        }
    }

    private void updateCamera() {
        this.camera.position.x = this.character.getSprite().getX();
        this.camera.position.y = this.character.getSprite().getY();

        // The left boundary of the map (x)
        int mapLeft = 0;
        // The right boundary of the map (x + width)
        int mapRight = 0 + mapWidth;
        // The bottom boundary of the map (y)
        int mapBottom = 0;
        // The top boundary of the map (y + height)
        int mapTop = 0 + mapHeight;
        // The camera dimensions, halved
        float cameraHalfWidth = this.camera.viewportWidth * 0.5f;
        float cameraHalfHeight = this.camera.viewportHeight * 0.5f;

        // Move camera after player as normal
        float cameraLeft = this.camera.position.x - cameraHalfWidth;
        float cameraRight = this.camera.position.x + cameraHalfWidth;
        float cameraBottom = this.camera.position.y - cameraHalfHeight;
        float cameraTop = this.camera.position.y + cameraHalfHeight;

        // Horizontal axis
        if(mapWidth < this.camera.viewportWidth) {
            this.camera.position.x = mapRight / 2;
        } else if(cameraLeft <= mapLeft) {
            this.camera.position.x = mapLeft + cameraHalfWidth;
        } else if(cameraRight >= mapRight) {
            this.camera.position.x = mapRight - cameraHalfWidth;
        }

        // Vertical axis
        if(mapHeight < this.camera.viewportHeight) {
            this.camera.position.y = mapTop / 2;
        } else if(cameraBottom <= mapBottom) {
            this.camera.position.y = mapBottom + cameraHalfHeight;
        } else if(cameraTop >= mapTop) {
            this.camera.position.y = mapTop - cameraHalfHeight;
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
