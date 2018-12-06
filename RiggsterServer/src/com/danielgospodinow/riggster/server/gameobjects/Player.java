package com.danielgospodinow.riggster.server.gameobjects;

public class Player {

    private int playerID;
    private String sprite;
    private String name;
    private Position position;

    public Player(int playerID, String sprite, String name, Position position) {
        this.playerID = playerID;
        this.sprite = sprite;
        this.name = name;
        this.position = position;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getSprite() {
        return this.sprite;
    }

    public String getName() {
        return this.name;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
