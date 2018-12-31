package com.danielgospodinow.riggster.server.gameobjects;

public class Enemy {

    private int id;
    private int row;
    private int col;
    private int health;
    private int damage;

    private int clientOwner = -1;
    private boolean isCurrentlyInUse = false;

    public Enemy(int id, int row, int col, int health, int damage) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.health = health;
        this.damage = damage;
    }

    public void updateInformation(int clientId, int row, int col, int health, boolean currentlyInUse) {
        this.clientOwner = clientId;
        this.row = row;
        this.col = col;
        this.health = health;
        this.isCurrentlyInUse = currentlyInUse;
    }

    public int getClientOwner() {
        return this.clientOwner;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public int getName() {
        return this.id;
    }

    public int getHealth() {
        return this.health;
    }

    public boolean isCurrentlyInUse() {
        return this.isCurrentlyInUse;
    }

    @Override
    public String toString() {
        return String.format("%d %d %d %d %d", this.id, this.row, this.col, this.health, this.damage);
    }
}
