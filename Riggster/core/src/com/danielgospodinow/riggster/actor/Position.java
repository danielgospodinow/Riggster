package com.danielgospodinow.riggster.actor;

public class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void addDirection(Position direction) {
        this.row += direction.row;
        this.col += direction.col;
    }

    @Override
    public Position clone() {
        return new Position(this.row, this.col);
    }
}
