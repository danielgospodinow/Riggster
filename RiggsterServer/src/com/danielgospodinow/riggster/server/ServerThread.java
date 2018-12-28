package com.danielgospodinow.riggster.server;

import com.danielgospodinow.riggster.server.gameobjects.Player;
import com.danielgospodinow.riggster.server.gameobjects.Position;
import com.danielgospodinow.riggster.server.gameobjects.Treasure;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class ServerThread {

    private Socket clientSocket;

    private DataInputStream reader;
    private DataOutputStream writer;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.reader = new DataInputStream(this.clientSocket.getInputStream());
            this.writer = new DataOutputStream(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Server failed to initialize reader/writer for client thread!");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            sendFilesToClient(this.clientSocket);
            System.out.println("Files send successfully!");
        } catch (IOException e) {
            System.out.println("Server failed to deliver files to client!");
            e.printStackTrace();
            System.exit(1);
        }

        new Thread(() -> {
            try {
                while(this.isConnected()) {
                    String message = this.reader.readUTF();

                    String[] args = message.split(" ");
                    switch (NetworkOperations.getOperation(args[0])) {
                        case CHARACTER_POSITION:
                            int newRow = Integer.parseInt(args[1]);
                            int newCol = Integer.parseInt(args[2]);

                            Server.getInstance().updatePlayerPosition(this.getPort(), new Position(newRow, newCol));
                            Server.getInstance().broadcastMessage(String.format("%s %d %d %d",
                                    NetworkOperations.CHARACTER_POSITION.toString(),
                                    this.getPort(),
                                    newRow,
                                    newCol),
                                    this);
                            break;

                        case CHARACTER_INITIALIZATION:
                            String sprite = args[1];
                            String characterName = args[2];
                            int initRow = Integer.parseInt(args[3]);
                            int initCol = Integer.parseInt(args[4]);

                            Player newPlayer = new Player(this.getPort(), sprite, characterName, new Position(initRow, initCol));

                            Server.getInstance().registerPlayer(this.getPort(), newPlayer);
                            Server.getInstance().broadcastMessage(String.format("%s %d %s %s %d %d",
                                    NetworkOperations.OTHER_CHARACTER_INITIALIZATION.toString(),
                                    this.getPort(),
                                    newPlayer.getSprite(),
                                    newPlayer.getName(),
                                    newPlayer.getPosition().row,
                                    newPlayer.getPosition().col),
                                    this);
                            Server.getInstance().sendOtherPlayers(this);
                            break;

                        case REMOVE_TREASURE:
                            int treasureX = Integer.parseInt(args[1]);
                            int treasureY = Integer.parseInt(args[2]);
                            int treasureWidth = Integer.parseInt(args[3]);
                            int treasureHeight = Integer.parseInt(args[4]);

                            Server.getInstance().removeTreasure(new Rectangle(treasureX, treasureY, treasureWidth,
                                    treasureHeight));
                            Server.getInstance().broadcastMessage(message, this);
                            break;

                        default:
                            System.out.println("Unknown command received! It's: " + args[0]);
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Server failed to read from client!");
                e.printStackTrace();
                this.close();
            }
        }).start();
    }

    public void writeMessage(String message) {
        try {
            this.writer.writeUTF(message);
            this.writer.flush();
        } catch (IOException e) {
            System.out.println("Server failed to send message to client!");
            e.printStackTrace();
            this.close();
        }
    }

    public boolean isConnected() {
        return this.clientSocket != null && this.clientSocket.isConnected();
    }

    public int getPort() {
        return this.clientSocket.getPort();
    }

    private synchronized void close() {
        if(this.clientSocket == null || this.writer == null || this.reader == null) {
            return;
        }

        try {
            this.writer.close();
            this.reader.close();
            this.clientSocket.close();
            Server.getInstance().removeClient(this);
            this.writer = null;
            this.reader = null;
            this.clientSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFilesToClient(Socket client) throws IOException {
        List<File> files = new LinkedList<>();
        Arrays.stream(Server.getInstance().MAP_FILE_EXTENSIONS)
            .map(fileExtension -> String.format("%s/%s.%s",
                    Server.getInstance().MAP_DIRECTORY,
                    Server.getInstance().MAP_NAME,
                    fileExtension))
            .forEach(mapFileName -> files.add(new File(mapFileName)));

        DataOutputStream dos = new DataOutputStream(client.getOutputStream());

        dos.writeUTF(String.format("%s.%s", Server.getInstance().MAP_NAME, Server.getInstance().MAP_MAIN_EXTENSION));
        dos.writeByte(Server.getInstance().MAP_FILE_EXTENSIONS.length);

        for(File file: files) {
            FileInputStream fis = new FileInputStream(file);

            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            byte[] buffer = new byte[8 * 1024];
            int readBytes;
            while((readBytes = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, readBytes);
            }
        }
    }
}