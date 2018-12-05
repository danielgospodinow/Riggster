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
import com.danielgospodinow.riggster.Character;
import com.danielgospodinow.riggster.Game;
import com.danielgospodinow.riggster.actor.Position;
import com.danielgospodinow.riggster.networking.Client;
import com.danielgospodinow.riggster.networking.NetworkOperations;
import com.danielgospodinow.riggster.networking.NetworkOperator;
import com.danielgospodinow.riggster.scenes.HUD;

import java.util.HashMap;

public class PlayScreen implements Screen {

    private static final int WORLD_WIDTH = 1200;
    private static final int WORLD_HEIGHT = 800;
    private static int MAP_WIDTH;
    private static int MAP_HEIGHT;
    private static int TILEMAP_WIDTH;
    private static int TILEMAP_HEIGHT;

    public static int getMapWidth() { return MAP_WIDTH; }
    public static int getMapHeight() { return  MAP_HEIGHT; }
    public static int getTilemapWidth() { return TILEMAP_WIDTH; }
    public static int getTilemapHeight() { return TILEMAP_HEIGHT; }

    private NetworkOperator networkOperator;

    private Game game;

    private HUD hud;

    private OrthographicCamera camera;
    private Viewport cameraViewport;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Character character;
    private HashMap<Integer, Character> otherCharacters;

    public PlayScreen(Game game) {
        this.game = game;

        loadNetwork();
        loadMap();
        loadCamera();
        loadCharacter();
        loadHud();
    }

    private void loadNetwork() {
        this.networkOperator = new NetworkOperator(new Client("localhost", 3000));
    }

    private void loadCharacter() {
        this.character = new Character("Character", 100, 100);
        this.character.getSprite().setPosition(0, MAP_HEIGHT - this.character.getSprite().getHeight());

        this.otherCharacters = new HashMap<Integer, Character>();
        //TODO: send a request to the server to get all other characters
    }

    private void loadMap() {
        this.mapLoader = new TmxMapLoader();
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.textureMinFilter = Texture.TextureFilter.Linear;
        this.map = this.mapLoader.load("map.tmx", params);
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.map);

        TILEMAP_WIDTH = this.map.getProperties().get("width", Integer.class);
        TILEMAP_HEIGHT = this.map.getProperties().get("height", Integer.class);

        MAP_WIDTH = (TILEMAP_WIDTH * this.map.getProperties().get("tilewidth", Integer.class));
        MAP_HEIGHT = (TILEMAP_HEIGHT * this.map.getProperties().get("tileheight", Integer.class));
    }

    private void loadHud() {
        this.hud = new HUD(this.game.spriteBatch);
    }

    private void loadCamera() {
        this.camera = new OrthographicCamera();
        this.cameraViewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, this.camera);

        Vector2 cameraStartPoint = new Vector2(this.cameraViewport.getWorldWidth() / 2,
                this.cameraViewport.getWorldHeight() / 2 + (float) MAP_HEIGHT / 2 - (this.cameraViewport.getWorldHeight() - (float) MAP_HEIGHT / 2));
        this.camera.position.set(cameraStartPoint, 0);
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        updateCamera();
        handleNetwork();

        this.camera.update();
        this.mapRenderer.setView(this.camera);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.mapRenderer.render();

        this.game.spriteBatch.setProjectionMatrix(this.hud.getStage().getCamera().combined);
        this.hud.getStage().draw();

        this.game.spriteBatch.begin();
        for(Character otherCharacter: this.otherCharacters.values()) {
            otherCharacter.getSprite().draw(this.game.spriteBatch);
        }
        this.character.getSprite().draw(this.game.spriteBatch);
        this.game.spriteBatch.end();

    }

    private void handleNetwork() {
        //TODO: move all that network logic in NetworkOperator
        String message = this.networkOperator.readMessage();
        if(message == null) {
            return;
        }

        String[] commands = message.split(" ");
        switch (NetworkOperations.getOperation(commands[0])) {
        case CHARACTER_POSITION:
            int characterID = Integer.parseInt(commands[1]);
            int row = Integer.parseInt(commands[2]);
            int col = Integer.parseInt(commands[3]);

            Character character = this.otherCharacters.get(characterID);
            if(character == null) {
                character = new Character(String.valueOf(characterID), 100, 100, new Position(row, col));
                character.getSprite().setPosition(0, MAP_HEIGHT - this.character.getSprite().getHeight());
                this.otherCharacters.put(characterID, character);
            } else {
                character.setPosition(new Position(row, col));
            }

            break;
        }
    }

    private void handleInput(float deltaTime) {
        boolean sucUp = false;
        boolean sucDown = false;
        boolean sucRight = false;
        boolean sucLeft = false;

        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            sucUp = this.character.move(Character.MoveDirection.UP);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            sucDown = this.character.move(Character.MoveDirection.DOWN);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            sucRight = this.character.move(Character.MoveDirection.RIGHT);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            sucLeft = this.character.move(Character.MoveDirection.LEFT);
        }

        if(sucUp || sucDown || sucRight || sucLeft) {
            this.networkOperator.sendMyNewPosition(character.getPosition());
        }
    }

    private void updateCamera() {
        this.camera.position.x = this.character.getSprite().getX();
        this.camera.position.y = this.character.getSprite().getY();

        int mapLeft = 0;
        int mapRight = 0 + MAP_WIDTH;
        int mapBottom = 0;
        int mapTop = 0 + MAP_HEIGHT;

        float cameraHalfWidth = this.camera.viewportWidth * 0.5f;
        float cameraHalfHeight = this.camera.viewportHeight * 0.5f;

        float cameraLeft = this.camera.position.x - cameraHalfWidth;
        float cameraRight = this.camera.position.x + cameraHalfWidth;
        float cameraBottom = this.camera.position.y - cameraHalfHeight;
        float cameraTop = this.camera.position.y + cameraHalfHeight;

        if(MAP_WIDTH < this.camera.viewportWidth) {
            this.camera.position.x = (float) mapRight / 2;
        } else if(cameraLeft <= mapLeft) {
            this.camera.position.x = mapLeft + cameraHalfWidth;
        } else if(cameraRight >= mapRight) {
            this.camera.position.x = mapRight - cameraHalfWidth;
        }

        if(MAP_HEIGHT < this.camera.viewportHeight) {
            this.camera.position.y = (float) mapTop / 2;
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
