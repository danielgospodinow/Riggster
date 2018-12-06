package com.danielgospodinow.riggster.networking;

import com.danielgospodinow.riggster.Character;
import com.danielgospodinow.riggster.actor.Position;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetworkOperator {

    private Client client;

    public NetworkOperator(Client client) {
        this.client = client;
    }

    public void startAsyncReading() {
        this.client.startAsyncReading();
    }

    public void updateOtherPlayers(Map<Integer, Character> otherCharacters) {
        String message = this.readMessage();
        if(message == null) {
            return;
        }

        String[] args = message.split(" ");
        switch (NetworkOperations.getOperation(args[0])) {
            case CHARACTER_POSITION:
                Integer characterID = Integer.parseInt(args[1]);
                int row = Integer.parseInt(args[2]);
                int col = Integer.parseInt(args[3]);

                Character character = otherCharacters.get(characterID);
                if(character != null) {
                    character.setPosition(new Position(row, col));
                }

                break;
            case OTHER_CHARACTER_INITIALIZATION:
                int initCharacterID = Integer.parseInt(args[1]);
                String sprite = args[2];
                String name = args[3];
                int initRow = Integer.parseInt(args[4]);
                int initCol = Integer.parseInt(args[5]);

                otherCharacters.put(initCharacterID, new Character(sprite, name, new Position(initRow, initCol)));
                break;
            case CHARACTER_DISPOSE:
                int disposedCharacterID = Integer.parseInt(args[1]);

                otherCharacters.remove(disposedCharacterID);
                break;
        }
    }

    public HashMap<Integer, Character> retrieveOtherCharacters() {
        HashMap<Integer, Character> characters = new HashMap<Integer, Character>();

        String[] playersInfo = new String[] {};
        try {
            playersInfo = this.client.getSocketInputStream().readUTF().split("@");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!(playersInfo.length == 0 || playersInfo.length == 1 && playersInfo[0].equals(""))) {
            for(int i=0; i<playersInfo.length; ++i) {
                String[] args = playersInfo[i].split(" ");

                int playerID = Integer.parseInt(args[0]);
                String sprite = args[1];
                String name = args[2];
                int row = Integer.parseInt(args[3]);
                int col = Integer.parseInt(args[4]);

                characters.put(playerID, new Character(sprite, name, new Position(row, col)));
            }
        }

        return characters;
    }

    public String readMessage() {
        return this.client.readMessage();
    }

    public void sendMyNewPosition(Position newPosition) {
        this.client.writeMessage(String.format("%s %d %d", NetworkOperations.CHARACTER_POSITION.toString(), newPosition.row, newPosition.col));
    }

    public void registerMyCharacter(Character character) {
        this.client.writeMessage(String.format("%s %s %s %d %d", NetworkOperations.CHARACTER_INITIALIZATION.toString(), character.getSpriteName(), character.getName(), character.getPosition().row, character.getPosition().col));
    }
}
