package com.danielgospodinow.riggster.networking;

import com.danielgospodinow.riggster.actor.Player;
import com.danielgospodinow.riggster.actor.Orc;
import com.danielgospodinow.riggster.actor.Position;

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

    public List<Orc> retrieveEnemies(Player character, NetworkOperator networkOperator) {
        this.client.writeMessage(String.format("%s", NetworkOperations.GET_ENEMIES.toString()));

        List<Orc> enemies = new ArrayList<Orc>();

        String[] enemiesInfo = new String[] {};
        try {
            enemiesInfo = this.client.getSocketInputStream().readUTF().split("@");
        } catch (IOException e) {
            System.out.println("Failed to retrieve enemies' information!");
            e.printStackTrace();
        }

        if(!(enemiesInfo.length == 0 || enemiesInfo.length == 1 && enemiesInfo[0].equals(""))) {
            for (String enemyInfo : enemiesInfo) {
                String[] args = enemyInfo.split(" ");

                int id = Integer.parseInt(args[0]);
                int row = Integer.parseInt(args[1]);
                int col = Integer.parseInt(args[2]);
                int health = Integer.parseInt(args[3]);
                int damage = Integer.parseInt(args[4]);

                enemies.add(new Orc(String.valueOf(id), row, col, health, damage, character, networkOperator));
            }
        }

        return enemies;
    }

    public HashMap<Integer, Player> retrieveOtherCharacters() {
        HashMap<Integer, Player> characters = new HashMap<Integer, Player>();

        String[] playersInfo = new String[] {};
        try {
            playersInfo = this.client.getSocketInputStream().readUTF().split("@");
        } catch (IOException e) {
            System.out.println("Failed to retrieve other characters' information!");
            e.printStackTrace();
        }

        if(!(playersInfo.length == 0 || playersInfo.length == 1 && playersInfo[0].equals(""))) {
            for (String playerInfo : playersInfo) {
                String[] args = playerInfo.split(" ");

                int playerID = Integer.parseInt(args[0]);
                String sprite = args[1];
                String name = args[2];
                int row = Integer.parseInt(args[3]);
                int col = Integer.parseInt(args[4]);

                characters.put(playerID, new Player(sprite, name, new Position(row, col)));
            }
        }

        return characters;
    }

//    public List<Rectangle> retrieveRemainingTreasures() {
//        List<Rectangle> remainingTreasures = new ArrayList<Rectangle>();

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

//        return remainingTreasures;
//    }

    public void updateEnemyStatus(Orc enemy, boolean currentlyInUse) {
        this.client.writeMessage(String.format("%s %s %d %d %d %s",
                NetworkOperations.ENEMY_UPDATED,
                enemy.getName(),
                enemy.getPosition().row,
                enemy.getPosition().col,
                enemy.getHealth(),
                currentlyInUse ? "t" : "f"));
    }

    public void removeEnemy(Orc enemy) {
        this.client.writeMessage(String.format("%s %s",
                NetworkOperations.ENEMY_DIED.toString(),
                enemy.getName()));
    }

    public void removeMyCharacter() {
        this.client.writeMessage(String.format("%s", NetworkOperations.CHARACTER_DISPOSE.toString()));
    }

    public void sendMyNewPosition(Position newPosition) {
        this.client.writeMessage(String.format("%s %d %d", NetworkOperations.CHARACTER_POSITION.toString(), newPosition.row, newPosition.col));
    }

    public void registerMyCharacter(Player character) {
        this.client.writeMessage(String.format("%s %s %s %d %d", NetworkOperations.CHARACTER_INITIALIZATION.toString(), character.getSpriteName(), character.getName(), character.getPosition().row, character.getPosition().col));
    }

    public void removeTreasure(Rectangle rectangle) {
        this.client.writeMessage(String.format("%s %d %d %d %d", NetworkOperations.REMOVE_TREASURE.toString(), rectangle.x, rectangle.y, rectangle.width, rectangle.height));
    }

    public String getMapFileName() {
        return this.client.getMapFileName();
    }

    public String readMessage() {
        return this.client.readMessage();
    }
}
