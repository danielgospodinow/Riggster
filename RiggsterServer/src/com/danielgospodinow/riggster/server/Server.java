package com.danielgospodinow.riggster.server;

import com.danielgospodinow.riggster.server.gameobjects.Position;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static final String[] FILES = {
            "map.tmx",
            "map.tsx",
            "map.png"
    };
    private static final int PORT = 3000;

    private static ServerSocket serverSocket;
    private static ConcurrentHashMap<Integer, ServerThread> clients;
    private static ConcurrentHashMap<Integer, Position> clientPositions;

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
        clientPositions = new ConcurrentHashMap<>();

        while(true) {
            Socket clientSocket = acceptClient();
            if(clientSocket == null) { continue; }

            System.out.println("Client connected! Port: " + clientSocket.getPort());
            ServerThread newClientThread = new ServerThread(clientSocket);
            clients.put(clientSocket.getPort(), newClientThread);
        }
    }

    public static synchronized void broadcastMessage(String message, ServerThread initiator) {
        for(ServerThread serverThread: clients.values()) {
            if (serverThread != initiator) {
                serverThread.writeMessage(message);
            }
        }
    }

    public static void removeClient(ServerThread serverThread) {
        clients.remove(serverThread.getPort());
        System.out.println(String.format("Client %d dropped out!", serverThread.getPort()));
    }

    public static void updatePlayerPosition(int playerID, Position position) {
        if(clientPositions.get(playerID) == null) {
            clientPositions.put(playerID, new Position(0 ,0));
        }

        clientPositions.put(playerID, position);
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