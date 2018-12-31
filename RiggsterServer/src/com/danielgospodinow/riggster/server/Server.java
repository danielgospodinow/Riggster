package com.danielgospodinow.riggster.server;

import com.danielgospodinow.riggster.server.gameobjects.Enemy;
import com.danielgospodinow.riggster.server.gameobjects.Player;
import com.danielgospodinow.riggster.server.gameobjects.Position;
import com.danielgospodinow.riggster.server.utils.MapLoader;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Server {

    private static final Server instance = new Server();
    public static Server getInstance() {
        return instance;
    }

    public final String MAP_DIRECTORY = "resources";
    public final String MAP_NAME = "map";
    public final String MAP_MAIN_EXTENSION = "tmx";
    public final String[] MAP_FILE_EXTENSIONS = {
        "tmx",
        "tsx",
        "png"
    };

    private ServerSocket serverSocket;

    //TODO: Move those to a database
    private ConcurrentHashMap<Integer, ServerThread> clients;
    private ConcurrentHashMap<Integer, Player> clientCharacters;
    private ConcurrentLinkedQueue<Rectangle> treasures;
    private ConcurrentLinkedQueue<Enemy> enemies;

    private Server() {

    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Failed to initialize server!");
            e.printStackTrace();
            System.exit(1);
        }

        clients = new ConcurrentHashMap<>();
        clientCharacters = new ConcurrentHashMap<>();
        treasures = new ConcurrentLinkedQueue<>(MapLoader.loadTreasures());
        enemies = new ConcurrentLinkedQueue<>(MapLoader.loadEnemies());

        while(true) {
            Socket clientSocket = acceptClient();
            if(clientSocket == null) { continue; }

            System.out.println("Client connected! Port: " + clientSocket.getPort());
            ServerThread newClientThread = new ServerThread(clientSocket);
            clients.put(clientSocket.getPort(), newClientThread);
        }
    }

    public void broadcastMessage(String message, ServerThread initiator) {
        for(ServerThread serverThread: clients.values()) {
            if (serverThread != initiator) {
                serverThread.writeMessage(message);
            }
        }
    }

    public void removeClient(ServerThread serverThread) {
        clients.remove(serverThread.getPort());
        clientCharacters.remove(serverThread.getPort());
        clients.values().forEach(otherClient -> otherClient.writeMessage(String.format("E %d", serverThread.getPort())));
        System.out.println(String.format("Client %d dropped out!", serverThread.getPort()));
    }

    public void updatePlayerPosition(int playerID, Position position) {
        Player character = clientCharacters.get(playerID);
        if(character != null) {
            character.setPosition(position);
        }
    }

    public void sendOtherPlayers(ServerThread serverThread) {
        String playersInformation = clientCharacters.values().stream()
                .filter(player -> player.getPlayerID() != serverThread.getPort())
                .map(player -> String.format("%d %s %s %d %d", player.getPlayerID(), player.getSprite(), player.getName(), player.getPosition().row, player.getPosition().col))
                .collect(Collectors.joining("@"));

        serverThread.writeMessage(playersInformation);
    }

    public void sendEnemies(ServerThread serverThread) {
        String enemiesInformation = enemies.stream()
                .map(Enemy::toString)
                .collect(Collectors.joining("@"));

        serverThread.writeMessage(enemiesInformation);
    }

    public void updateEnemy(ServerThread initiator, int clientId, String name, int row, int col, int health, boolean currentlyInUse) {
        Enemy currentEnemy = this.enemies.stream().filter(enemy -> String.valueOf(enemy.getName()).equals(name)).findFirst().orElseThrow();

        if(clientId == currentEnemy.getClientOwner()) {
            currentEnemy.updateInformation(clientId, row, col, health, currentlyInUse);
        } else {
            if(!currentEnemy.isCurrentlyInUse()) {
                currentEnemy.updateInformation(clientId, row, col, health, currentlyInUse);
            }
        }

        this.broadcastMessage(String.format("%s %d %d %d %d %s",
                NetworkOperations.ENEMY_UPDATED,
                currentEnemy.getName(),
                currentEnemy.getRow(),
                currentEnemy.getCol(),
                currentEnemy.getHealth(),
                currentEnemy.isCurrentlyInUse() ? "t" : "f"), initiator);
    }

    public void removeEnemy(ServerThread initiator, String diedEnemyName) {
        Iterator<Enemy> enemyIterator = this.enemies.iterator();
        while(enemyIterator.hasNext()) {
            Enemy currentEnemy = enemyIterator.next();
            if(String.valueOf(currentEnemy.getName()).equals(diedEnemyName)) {
                enemyIterator.remove();
                this.broadcastMessage(String.format("%s %s",
                        NetworkOperations.ENEMY_DIED.toString(),
                        diedEnemyName), initiator);
                break;
            }
        }
    }

    public void registerPlayer(int playerID, Player player) {
        clientCharacters.put(playerID, player);
    }

//    public void removeTreasure(Rectangle treasure) {
//        this.treasures.remove(treasure);
//    }

    private Socket acceptClient() {
        Socket socket = null;

        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Server failed to initialize a client!");
            e.printStackTrace();
        }

        return socket;
    }
}