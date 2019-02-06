package com.danielgospodinow.riggster.server.utils;

public enum NetworkOperations {

    CHARACTER_POSITION("P"),
    CHARACTER_INITIALIZATION("C"),
    CHARACTER_DISPOSE("E"),
    OTHER_CHARACTER_INITIALIZATION("O"),
    REMOVE_TREASURE("T"),
    GET_ENEMIES("Z"),
    ENEMY_DIED("D"),
    ENEMY_UPDATED("H"),
    UNKNOWN("U");

    public static NetworkOperations getOperation(String commandString) {
        switch (commandString) {
            case "P": return CHARACTER_POSITION;
            case "C": return CHARACTER_INITIALIZATION;
            case "O": return OTHER_CHARACTER_INITIALIZATION;
            case "T": return REMOVE_TREASURE;
            case "Z": return GET_ENEMIES;
            case "E": return CHARACTER_DISPOSE;
            case "D": return ENEMY_DIED;
            case "H": return ENEMY_UPDATED;
            default: return UNKNOWN;
        }
    }

    private final String commandString;

    NetworkOperations(String str) {
        this.commandString = str;
    }

    @Override
    public String toString() {
        return this.commandString;
    }
}
