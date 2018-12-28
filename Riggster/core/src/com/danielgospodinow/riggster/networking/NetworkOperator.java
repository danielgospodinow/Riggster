package com.danielgospodinow.riggster.networking;

import com.danielgospodinow.riggster.Character;
import com.danielgospodinow.riggster.actor.Position;
import com.danielgospodinow.riggster.treasure.Treasure;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class NetworkOperator {

    private Client client;

    public NetworkOperator(Client client) {
        this.client = client;
    }

    public void startAsyncReading() {
        this.client.startAsyncReading();
    }

    public String[] fetchGameUpdateOperation() {
        String message = this.readMessage();
        if(message == null) {
            return null;
        }

        return message.split(" ");
    }

    public HashMap<Integer, Character> retrieveOtherCharacters() {
        HashMap<Integer, Character> characters = new HashMap<Integer, Character>();

        String[] playersInfo = new String[] {};
        try {
            playersInfo = this.client.getSocketInputStream().readUTF().split("@");
        } catch (IOException e) {
            System.out.println("Failed to retrieve other characters' information!");
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

    public List<Rectangle> retrieveRemainingTreasures() {
        List<Rectangle> remainingTreasures = new ArrayList<Rectangle>();

//        String[] treasuresInfo = new String[] {};
//        try {
//            treasuresInfo = this.client.getSocketInputStream().readUTF().split("@");
//        } catch (IOException e) {
//            System.out.println("Failed to retrieve remaining treasures information!");
//            e.printStackTrace();
//        }
//
//        if(!(treasuresInfo.length == 0 || treasuresInfo.length == 1 && treasuresInfo[0].equals(""))) {
//            for(int i=0; i<treasuresInfo.length; ++i) {
//                String[] args = treasuresInfo[i].split(" ");
//
//                int x = Integer.parseInt(args[0]);
//                int y = Integer.parseInt(args[1]);
//                int width = Integer.parseInt(args[2]);
//                int height = Integer.parseInt(args[3]);
//
//                remainingTreasures.add(new Rectangle(x, y, width, height));
//            }
//        }

        return remainingTreasures;
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

    public void removeTreasure(Rectangle rectangle) {
        this.client.writeMessage(String.format("%s %d %d %d %d", NetworkOperations.REMOVE_TREASURE.toString(), rectangle.x, rectangle.y, rectangle.width, rectangle.height));
    }

    public String getMapFileName() {
        return this.client.getMapFileName();
    }
}
