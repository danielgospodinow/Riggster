package com.danielgospodinow.riggster.server;

import com.danielgospodinow.riggster.server.gameobjects.Position;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
                        String name = args[1];
                        int row = Integer.parseInt(args[2]);
                        int col = Integer.parseInt(args[3]);

                        Server.updatePlayerPosition(this.getPort(), new Position(row, col));
                        Server.broadcastMessage(String.format("P %s %d %d", name, row, col), this);
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
            Server.removeClient(this);
            this.writer = null;
            this.reader = null;
            this.clientSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFilesToClient(Socket client) throws IOException {
        List<File> files = new LinkedList<>();
        Arrays.stream(Server.FILES).forEach(str -> files.add(new File(str)));

        DataOutputStream dos = new DataOutputStream(client.getOutputStream());

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