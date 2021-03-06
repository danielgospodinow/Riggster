package com.danielgospodinow.riggster.server;

import com.danielgospodinow.riggster.server.gameobjects.Player;
import com.danielgospodinow.riggster.server.gameobjects.Position;
import com.danielgospodinow.riggster.server.utils.Logger;
import com.danielgospodinow.riggster.server.utils.NetworkOperations;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerRunnable implements Runnable {

    private Socket clientSocket;

    private DataInputStream reader;
    private DataOutputStream writer;

    public ServerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.reader = new DataInputStream(this.clientSocket.getInputStream());
            this.writer = new DataOutputStream(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            Logger.getInstance().logError("Server failed to initialize reader/writer for client thread!", e);
            System.exit(1);
        }

        try {
            sendFilesToClient(this.clientSocket);
        } catch (IOException e) {
            Logger.getInstance().logError("Server failed to deliver files to client!", e);
        }

        try {
            sendTreasuresToClient(this.clientSocket);
        } catch (IOException e) {
            Logger.getInstance().logError("Server failed to deliver remaining treasures to client!", e);
        }
    }

    public void writeMessage(String message) {
        try {
            this.writer.writeUTF(message);
            this.writer.flush();
        } catch (IOException e) {
            Logger.getInstance().logError("Server failed to send message to client!", e);
            this.close();
        }
    }

    public boolean isConnected() {
        return this.clientSocket != null && !this.clientSocket.isClosed();
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
            Logger.getInstance().logError("Failed to close server runnable resources!", e);
        }
    }

    private static void sendTreasuresToClient(Socket client) throws IOException {
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());

        StringBuilder sb = new StringBuilder();
        Server.getInstance().getTreasureIds().forEach(integer -> {
            sb.append(integer);
            sb.append("@");
        });

        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            dos.writeUTF(sb.toString());
        } else {
            dos.writeUTF("");
        }
    }

    private static void sendFilesToClient(Socket client) throws IOException {
        List<File> files = new LinkedList<>();
        Arrays.stream(Server.MAP_FILE_EXTENSIONS)
                .map(fileExtension -> String.format("%s/%s.%s",
                        Server.MAP_DIRECTORY,
                        Server.MAP_NAME,
                        fileExtension))
                .forEach(mapFileName -> files.add(new File(mapFileName)));

        DataOutputStream dos = new DataOutputStream(client.getOutputStream());

        dos.writeUTF(String.format("%s.%s", Server.MAP_NAME, Server.MAP_MAIN_EXTENSION));
        dos.writeByte(Server.MAP_FILE_EXTENSIONS.length);

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

    @Override
    public void run() {
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
                        int treasureId = Integer.parseInt(args[1]);

                        Server.getInstance().removeTreasure(treasureId);
                        Server.getInstance().broadcastMessage(message, this);
                        break;

                    case GET_ENEMIES:
                        Server.getInstance().sendEnemies(this);
                        break;

                    case CHARACTER_DISPOSE:
                        Server.getInstance().removeClient(this);
                        break;

                    case ENEMY_UPDATED:
                        String enemyName = args[1];
                        int enemyNewRow = Integer.parseInt(args[2]);
                        int enemyNewCol = Integer.parseInt(args[3]);
                        int enemyHealth = Integer.parseInt(args[4]);
                        String currentlyInUseStr = args[5];

                        Server.getInstance().updateEnemy(this, this.getPort(), enemyName, enemyNewRow, enemyNewCol, enemyHealth, currentlyInUseStr.equals("t"));
                        break;

                    case ENEMY_DIED:
                        String diedEnemyName = args[1];

                        Server.getInstance().removeEnemy(this, diedEnemyName);
                        break;

                    default:
                        System.out.println("Unknown command received! It's: " + args[0]);
                        break;
                }
            }
        } catch (IOException e) {
            Logger.getInstance().logError("Server failed to read from client!", e);
            this.close();
        }
    }
}
