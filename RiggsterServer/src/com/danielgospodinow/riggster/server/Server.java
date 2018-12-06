package com.danielgospodinow.riggster.server;

import com.danielgospodinow.riggster.server.gameobjects.Player;
import com.danielgospodinow.riggster.server.gameobjects.Position;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Server {

    public static final String[] FILES = {
            "map.tmx",
            "map.tsx",
            "map.png"
    };
    private static final int PORT = 3000;

    private static ServerSocket serverSocket;
    private static ConcurrentHashMap<Integer, ServerThread> clients;
    private static ConcurrentHashMap<Integer, Player> clientCharacters;

    public static void main(String[] args) {
        System.out.println(String.format("'Riggster' server starting on port: %d", PORT));

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Failed to initialize server!");
            e.printStackTrace();
            System.exit(1);
        }

        clients = new ConcurrentHashMap<>();
        clientCharacters = new ConcurrentHashMap<>();

        while(true) {
            Socket clientSocket = acceptClient();
            if(clientSocket == null) { continue; }

            System.out.println("Client connected! Port: " + clientSocket.getPort());
            ServerThread newClientThread = new ServerThread(clientSocket);
            clients.put(clientSocket.getPort(), newClientThread);
        }
    }

    public static void broadcastMessage(String message, ServerThread initiator) {
        for(ServerThread serverThread: clients.values()) {
            if (serverThread != initiator) {
                serverThread.writeMessage(message);
            }
        }
    }

    public static void removeClient(ServerThread serverThread) {
        clients.remove(serverThread.getPort());
        clientCharacters.remove(serverThread.getPort());
        clients.values().stream().forEach(otherClient -> otherClient.writeMessage(String.format("E %d", serverThread.getPort())));
        System.out.println(String.format("Client %d dropped out!", serverThread.getPort()));
    }

    public static void updatePlayerPosition(int playerID, Position position) {
        Player character = clientCharacters.get(playerID);
        if(character != null) {
            character.setPosition(position);
        }
    }

    public static void sendOtherPlayers(ServerThread serverThread) {
        String playersInformation = clientCharacters.values().stream()
                .filter(player -> player.getPlayerID() != serverThread.getPort())
                .map(player -> String.format("%d %s %s %d %d", player.getPlayerID(), player.getSprite(), player.getName(), player.getPosition().row, player.getPosition().col))
                .collect(Collectors.joining("@"));

        serverThread.writeMessage(playersInformation);
    }

    public static void registerPlayer(int playerID, Player player) {
        clientCharacters.put(playerID, player);
    }

    private static Socket acceptClient() {
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