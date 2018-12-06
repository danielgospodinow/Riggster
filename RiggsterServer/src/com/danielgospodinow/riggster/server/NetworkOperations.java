package com.danielgospodinow.riggster.server;

public enum NetworkOperations {

    CHARACTER_POSITION("P"),
    CHARACTER_INITIALIZATION("C"),
    CHARACTER_DISPOSE("E"),
    UNKNOWN("U");

    public static NetworkOperations getOperation(String commandString) {
        switch (commandString) {
            case "P": return CHARACTER_POSITION;
            case "C": return CHARACTER_INITIALIZATION;
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
