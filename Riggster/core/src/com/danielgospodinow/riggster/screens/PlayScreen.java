package com.danielgospodinow.riggster.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.danielgospodinow.riggster.Character;
import com.danielgospodinow.riggster.Game;
import com.danielgospodinow.riggster.actor.Position;
import com.danielgospodinow.riggster.networking.Client;
import com.danielgospodinow.riggster.networking.NetworkOperations;
import com.danielgospodinow.riggster.networking.NetworkOperator;
import com.danielgospodinow.riggster.scenes.HUD;
import com.danielgospodinow.riggster.treasure.Treasure;
import com.danielgospodinow.riggster.utils.RandomNumberGenerator;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PlayScreen implements Screen {

    private static final int WORLD_WIDTH = 1200;
    private static final int WORLD_HEIGHT = 800;
    private static int MAP_WIDTH = 1200;
    private static int MAP_HEIGHT = 800;

    public static int getMapWidth() { return MAP_WIDTH; }
    public static int getMapHeight() { return  MAP_HEIGHT; }

    private NetworkOperator networkOperator;

    private Game game;

    private HUD hud;

    private OrthographicCamera camera;
    private Viewport cameraViewport;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private List<Rectangle> staticObjects = new ArrayList<Rectangle>();
    private HashMap<Rectangle, Treasure> treasureObjects = new HashMap<Rectangle, Treasure>();

    private Character character;
    private String characterName;
    private HashMap<Integer, Character> otherCharacters;

    public PlayScreen(Game game, String playerName) {
        this.game = game;
        this.characterName = playerName;

        loadNetwork();
        loadMap();
        loadCamera();
        loadCharacter();
        loadHud();

        this.networkOperator.startAsyncReading();
    }

    private void loadNetwork() {
        this.networkOperator = new NetworkOperator(new Client("34.254.231.134", 3000));
    }

    private void loadMap() {
        this.mapLoader = new TmxMapLoader();
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.textureMinFilter = Texture.TextureFilter.Linear;
        this.map = this.mapLoader.load(this.networkOperator.getMapFileName(), params);
        this.map = this.mapLoader.load("map.tmx", params);
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.map);

        // Load static objects from the map
        MapObjects staticMapObjects = this.map.getLayers().get(3).getObjects();
        for (MapObject mapObject : staticMapObjects) {
            int x = (int) (float) ((Float) mapObject.getProperties().get("x"));
            int y = (int) (float) ((Float) mapObject.getProperties().get("y"));
            int width = (int) (float) ((Float) mapObject.getProperties().get("width"));
            int height = (int) (float) ((Float) mapObject.getProperties().get("height"));

            this.staticObjects.add(new Rectangle(x, y, width, height));
        }

        // Load treasure objects from map
        MapObjects treasureMapObjects = this.map.getLayers().get(4).getObjects();
        for (MapObject mapObject : treasureMapObjects) {
            int x = Math.round((Float) mapObject.getProperties().get("x"));
            int y = Math.round((Float) mapObject.getProperties().get("y"));
            int width = Math.round((Float) mapObject.getProperties().get("width"));
            int height = Math.round((Float) mapObject.getProperties().get("height"));

            this.treasureObjects.put(new Rectangle(x, y, width, height), Treasure.getRandomTreasure());
        }
//        List<Rectangle> remainingTreasures = this.networkOperator.retrieveRemainingTreasures();
//        this.treasureObjects.keySet().retainAll(remainingTreasures);
    }

    private void loadCamera() {
        this.camera = new OrthographicCamera();
        this.cameraViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, this.camera);

        Vector2 cameraStartPoint = new Vector2(this.cameraViewport.getWorldWidth() / 2,
                this.cameraViewport.getWorldHeight() / 2 + (float) MAP_HEIGHT / 2 - (this.cameraViewport.getWorldHeight() - (float) MAP_HEIGHT / 2));
        this.camera.position.set(cameraStartPoint, 0);
    }

    private void loadCharacter() {
        final int totalSprites = 4;
        int randomNum = RandomNumberGenerator.getRandomInt(1, totalSprites);

        this.character = new Character(String.format("character%d", randomNum), this.characterName, new Position(1, 1),
                this.staticObjects, this.treasureObjects);
        this.networkOperator.registerMyCharacter(this.character);
        this.otherCharacters = this.networkOperator.retrieveOtherCharacters();
    }

    private void loadHud() {
        this.hud = new HUD(this.game.spriteBatch, this.character);
    }

    @Override
    public void render(float delta) {
        // Handle input
        handleInput(delta);

        // Update camera
        updateCamera();
        this.camera.update();
        this.mapRenderer.setView(this.camera);

        // Clear the screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update map
        this.updateMap();

        // Render map
        this.mapRenderer.render();

        // Render the HUD
        this.game.spriteBatch.setProjectionMatrix(this.hud.getStage().getCamera().combined);
        this.hud.update();
        this.hud.getStage().draw();

        // Handle game network updates
        this.handleGameNetworkUpdates();

        // Draw everything
        this.game.spriteBatch.begin();
        for(Character otherCharacter: this.otherCharacters.values()) {
            otherCharacter.draw(this.game.spriteBatch);
        }
        this.character.draw(this.game.spriteBatch);
        this.game.spriteBatch.end();
    }

    private void handleGameNetworkUpdates() {
        String[] args = this.networkOperator.fetchGameUpdateOperation();
        if(args == null) {
            return;
        }

        switch (NetworkOperations.getOperation(args[0])) {
            case CHARACTER_POSITION:
                Integer characterID = Integer.parseInt(args[1]);
                int row = Integer.parseInt(args[2]);
                int col = Integer.parseInt(args[3]);

                Character character = otherCharacters.get(characterID);
                if(character != null) {
                    character.setPosition(new Position(row, col));
                }
                break;

            case OTHER_CHARACTER_INITIALIZATION:
                int initCharacterID = Integer.parseInt(args[1]);
                String sprite = args[2];
                String name = args[3];
                int initRow = Integer.parseInt(args[4]);
                int initCol = Integer.parseInt(args[5]);

                otherCharacters.put(initCharacterID, new Character(sprite, name, new Position(initRow, initCol)));
                break;

            case CHARACTER_DISPOSE:
                int disposedCharacterID = Integer.parseInt(args[1]);

                otherCharacters.remove(disposedCharacterID);
                break;

            case REMOVE_TREASURE:
                int treasureX = Integer.parseInt(args[1]);
                int treasureY = Integer.parseInt(args[2]);
                int treasureWidth = Integer.parseInt(args[3]);
                int treasureHeight = Integer.parseInt(args[4]);
                //TODO: FIX y coordinate
                Rectangle usedTreasure = new Rectangle(treasureX, treasureY, treasureWidth, treasureHeight);

                this.treasureObjects.remove(usedTreasure);
                break;
        }
    }

    private void updateMap() {
        // Remove collected treasures from map
        Iterator<Map.Entry<Rectangle, Treasure>> treasureEntryIterator = this.treasureObjects.entrySet().iterator();
        while(treasureEntryIterator.hasNext()) {
            Map.Entry<Rectangle, Treasure> currentTreasureEntry = treasureEntryIterator.next();
            if(currentTreasureEntry.getValue().isCollected()) {
                treasureEntryIterator.remove();

                Rectangle currentTreasureRectangle = currentTreasureEntry.getKey();
                currentTreasureRectangle.y = MAP_HEIGHT - currentTreasureRectangle.height - currentTreasureRectangle.y;
                this.networkOperator.removeTreasure(currentTreasureRectangle);
            }
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
