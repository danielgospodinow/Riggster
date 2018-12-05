package com.danielgospodinow.riggster.engine;

import com.danielgospodinow.riggster.actor.Position;

public enum Direction {
    UP      (new Position(-1,0)),
    DOWN    (new Position(+1,0)),
    LEFT    (new Position(0,-1)),
    RIGHT   (new Position(0,+1));

    private final Position direction;

    Direction(final Position position) {
        this.direction = position;
    }

    public Position getDirection() {
        return this.direction;
    }
}
