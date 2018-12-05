package com.danielgospodinow.riggster.server;

public enum NetworkOperations {

    CHARACTER_POSITION("P"),
    UNKNOWN("U");

    public static NetworkOperations getOperation(String commandString) {
        switch (commandString) {
            case "P": return CHARACTER_POSITION;
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
