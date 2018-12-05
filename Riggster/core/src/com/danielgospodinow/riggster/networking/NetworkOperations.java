package com.danielgospodinow.riggster.networking;

public enum NetworkOperations {

    CHARACTER_POSITION("P"),
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
