package com.danielgospodinow.riggster.server;

import com.danielgospodinow.riggster.server.gameobjects.Enemy;
import com.danielgospodinow.riggster.server.gameobjects.Player;
import com.danielgospodinow.riggster.server.gameobjects.Position;
import com.danielgospodinow.riggster.server.utils.Logger;
import com.danielgospodinow.riggster.server.utils.MapLoader;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Server {

    private static final Server instance = new Server();

    public static Server getInstance() {
        return instance;
    }

    public static final String MAP_DIRECTORY = "resources";
    public static final String MAP_NAME = "map";
    public static final String MAP_MAIN_EXTENSION = "tmx";
    public static final String[] MAP_FILE_EXTENSIONS = {
            "tmx",
            "tsx",
            "png"
    };

    private static final int MAX_CLIENT_THREADS = 5;
    private static final ThreadPoolExecutor executor =
            (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_CLIENT_THREADS);

    private ServerSocket serverSocket;

    private ConcurrentHashMap<Integer, ServerRunnable> clients;
    private ConcurrentHashMap<Integer, Player> clientCharacters;
    private ConcurrentLinkedQueue<Rectangle> treasures;
    private ConcurrentLinkedQueue<Enemy> enemies;

    private Server() {
        this.clients = new ConcurrentHashMap<>();
        this.clientCharacters = new ConcurrentHashMap<>();
        this.treasures = new ConcurrentLinkedQueue<>(MapLoader.loadTreasures());
        this.enemies = new ConcurrentLinkedQueue<>(MapLoader.loadEnemies());
    }

    public void startServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Logger.getInstance().logError("Failed to initialize server!", e);
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Performing shutdown cleanup...");

            executor.shutdown();

            while (true) {
                try {
                    System.out.println("Waiting for the service to terminate...");
                    if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        break;
                    }
                } catch (InterruptedException e) {
                    System.out.println("System failed to cleanup");
                }
            }
            System.out.println("Done cleaning");
        }));

        while (true) {
            Socket clientSocket = acceptClient();
            if (clientSocket == null) {
                continue;
            }

            if (executor.getActiveCount() >= MAX_CLIENT_THREADS) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    Logger.getInstance().logError("Failed to stop executor!", e);
                }
                continue;
            }

            System.out.println("Client connected! Port: " + clientSocket.getPort());
            ServerRunnable serverThread = new ServerRunnable(clientSocket);
            this.clients.put(clientSocket.getPort(), serverThread);

            executor.execute(serverThread);
        }
    }

    public void broadcastMessage(String message, ServerRunnable initiator) {
        for (ServerRunnable serverThread : this.clients.values()) {
            if (serverThread != initiator) {
                serverThread.writeMessage(message);
            }
        }
    }

    public void removeClient(ServerRunnable serverThread) {
        this.clients.remove(serverThread.getPort());
        this.clientCharacters.remove(serverThread.getPort());
        this.clients.values().forEach(otherClient -> otherClient.writeMessage(String.format("E %d",
                serverThread.getPort())));
        System.out.println(String.format("Client %d dropped out!", serverThread.getPort()));
    }

    public void updatePlayerPosition(int playerID, Position position) {
        Player character = this.clientCharacters.get(playerID);
        if (character != null) {
            character.setPosition(position);
        }
    }

    public void sendOtherPlayers(ServerRunnable serverThread) {
        String playersInformation = this.clientCharacters.values().stream()
                .filter(player -> player.getPlayerID() != serverThread.getPort())
                .map(player -> String.format("%d %s %s %d %d",
                        player.getPlayerID(),
                        player.getSprite(),
                        player.getName(),
                        player.getPosition().row,
                        player.getPosition().col))
                .collect(Collectors.joining("@"));

        serverThread.writeMessage(playersInformation);
    }

    public void sendEnemies(ServerRunnable serverThread) {
        String enemiesInformation = this.enemies.stream()
                .map(Enemy::toString)
                .collect(Collectors.joining("@"));

        serverThread.writeMessage(enemiesInformation);
    }

    public void updateEnemy(ServerRunnable initiator, int clientId, String name, int row, int col, int health,
                            boolean currentlyInUse) {
        Enemy currentEnemy = this.enemies.stream()
                .filter(enemy -> String.valueOf(enemy.getName()).equals(name))
                .findFirst()
                .orElseThrow();

        if (clientId == currentEnemy.getClientOwner()) {
            currentEnemy.updateInformation(clientId, row, col, health, currentlyInUse);
        } else {
            if (!currentEnemy.isCurrentlyInUse()) {
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

    public void removeEnemy(ServerRunnable initiator, String diedEnemyName) {
        Iterator<Enemy> enemyIterator = this.enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy currentEnemy = enemyIterator.next();
            if (String.valueOf(currentEnemy.getName()).equals(diedEnemyName)) {
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
            socket = this.serverSocket.accept();
        } catch (IOException e) {
            Logger.getInstance().logError("Server failed to initialize a client!", e);
        }

        return socket;
    }
}