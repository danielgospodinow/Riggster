package com.danielgospodinow.riggster.engine;

import com.danielgospodinow.riggster.actor.Position;

public class Map {

    private MapObjects[][] map;
    private int rows;
    private int cols;
    private Position heroPosition;

    public Map(char[][] map) {
        this.rows = map.length;
        this.cols = map[0].length;
        this.map = getSexyMap(map);
        this.heroPosition = this.getHeroPosition();
    }

    public MapObjects getObjectAt(Position position) {
        return this.map[position.row][position.col];
    }

    public void moveHero(Position newPosition) {
        this.clearPosition(this.heroPosition);
        this.map[newPosition.row][newPosition.col] = MapObjects.CURRENT_HERO_POSITION;
        this.heroPosition = newPosition;
    }

    public void clearPosition(Position position) {
        this.map[position.row][position.col] = MapObjects.EMPTY_SPOT;
    }

    public Position getHeroPosition() {
        if(this.heroPosition != null) {
            return this.heroPosition;
        }

        for(int i = 0; i<this.map.length; ++i) {
            for(int j = 0; j<this.map[0].length; ++j) {
                if(this.map[i][j] == MapObjects.START_HERO_POSITION) {
                    return new Position(i, j);
                }
            }
        }

        return null;
    }

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
    }

    public char[][] getRawMap() {
        char[][] rawMap = new char[rows][cols];
        for(int i=0; i<rows; ++i) {
            for(int j=0; j<cols; ++j) {
                rawMap[i][j] = this.map[i][j].getChar();
            }
        }

        return rawMap;
    }

    private MapObjects[][] getSexyMap(char[][] map) {
        MapObjects[][] retMap = new MapObjects[rows][cols];
        for(int i=0; i<rows; ++i) {
            for(int j=0; j<cols; ++j) {
                retMap[i][j] = MapObjects.getMapObjectByChar(map[i][j]);
            }
        }

        return retMap;
    }
}
