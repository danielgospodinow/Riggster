package com.danielgospodinow.riggster.networking;

import com.danielgospodinow.riggster.actor.Position;

public class NetworkOperator {

    private Client client;

    public NetworkOperator(Client client) {
        this.client = client;
    }

    public String readMessage() {
        return this.client.readMessage();
    }

    public void sendMyNewPosition(String name, Position newPosition) {
        this.client.writeMessage(String.format("%s %s %d %d", NetworkOperations.CHARACTER_POSITION.toString(), name,
                newPosition.row, newPosition.col));
    }
}
