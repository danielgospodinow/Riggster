package com.danielgospodinow.riggster.server;

public enum NetworkOperations {

    CHARACTER_POSITION("P"),
    CHARACTER_INITIALIZATION("C"),
    OTHER_CHARACTER_INITIALIZATION("O"),
    REMOVE_TREASURE("T"),
    UNKNOWN("U");

    public static NetworkOperations getOperation(String commandString) {
        switch (commandString) {
            case "P": return CHARACTER_POSITION;
            case "C": return CHARACTER_INITIALIZATION;
            case "O": return OTHER_CHARACTER_INITIALIZATION;
            case "T": return REMOVE_TREASURE;
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
