package com.danielgospodinow.riggster.networking;

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
        for (NetworkOperations oper: NetworkOperations.values()) {
            if(oper.toString().equals(commandString)) {
                return oper;
            }
        }

        return UNKNOWN;
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
