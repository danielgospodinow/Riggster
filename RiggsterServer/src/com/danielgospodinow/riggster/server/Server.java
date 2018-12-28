package com.danielgospodinow.riggster.server;

import com.danielgospodinow.riggster.server.gameobjects.Player;
import com.danielgospodinow.riggster.server.gameobjects.Position;
import com.danielgospodinow.riggster.server.utils.MapLoader;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
        clients.values().stream().forEach(otherClient -> otherClient.writeMessage(String.format("E %d", serverThread.getPort())));
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

    public void registerPlayer(int playerID, Player player) {
        clientCharacters.put(playerID, player);
    }

    public void removeTreasure(Rectangle treasure) {
        this.treasures.remove(treasure);
    }

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