package com.danielgospodinow.riggster.engine;

public enum MapObjects {

    START_HERO_POSITION     ('S'),
    CURRENT_HERO_POSITION   ('H'),
    OBSTACLE                ('#'),
    TREASURE                ('T'),
    EMPTY_SPOT              ('.'),
    ENEMY                   ('E'),
    EXIT                    ('G');

    public static MapObjects getMapObjectByChar(char character) {
        switch (character) {
            case 'S': return START_HERO_POSITION;
            case 'H': return CURRENT_HERO_POSITION;
            case '#': return OBSTACLE;
            case 'T': return TREASURE;
            case '.': return EMPTY_SPOT;
            case 'E': return ENEMY;
            case 'G': return EXIT;
            default: return null;
        }
    }

    private final char character;

    MapObjects(final char character) {
        this.character = character;
    }

    public char getChar() {
        return this.character;
    }
}
