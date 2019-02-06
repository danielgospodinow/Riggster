package com.danielgospodinow.riggster.networking;

import com.danielgospodinow.riggster.utils.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;

public class Client {

    private static final int PORT = 3000;

    private Socket clientSocket = null;
    private DataInputStream reader = null;
    private DataOutputStream writer = null;
    private LinkedList<String> receivedMessages = null;

    private String mapFileName;

    public Client() {

    }

    public void connect(String ip) throws IOException {
        this.clientSocket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(ip, PORT);

        try {
            this.clientSocket.connect(socketAddress);
        } catch (IOException e) {
            this.printError("Client failed to connect with the server!", e);
            System.exit(1);
        }

        try {
            this.reader = new DataInputStream(this.clientSocket.getInputStream());
            this.writer = new DataOutputStream(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            this.printError("Client failed to initialize reading/writing streams!", e);
            System.exit(1);
        }

        this.receiveFilesFromServer(clientSocket);

        this.receivedMessages = new LinkedList<String>();
    }

    public void startAsyncReading() {
        class ReaderTask implements Runnable {
            Client client;

            ReaderTask(Client client) {
                this.client = client;
            }

            @Override
            public void run() {
                try {
                    while (this.client.isConnected()) {
                        this.client.receivedMessages.add(this.client.reader.readUTF());
                    }
                } catch (IOException e) {
                    this.client.printError("Client failed to read from socket!", e);
                    this.client.close();
                }
            }
        }

        new Thread(new ReaderTask(this)).start();
    }

    public boolean writeMessage(String message) {
        if (!this.isConnected()) {
            this.printError("Sending message failed! Socket not connected!", null);
            return false;
        }

        try {
            this.writer.writeUTF(message);
            this.writer.flush();
        } catch (IOException e) {
            this.printError("Client failed to send message to socket!", e);
            this.close();
            return false;
        }

        return true;
    }

    public String readMessage() {
        if (!this.isConnected()) {
            this.printError("Receiving message failed! Socket not connected!", null);
            return null;
        }

        return (this.receivedMessages.size() > 0) ? this.receivedMessages.removeFirst() : null;
    }

    public synchronized void close() {
        if (this.clientSocket == null || this.writer == null || this.reader == null) {
            this.printError("Socket and streams already cleaned up!", null);
            return;
        }

        try {
            this.writer.close();
            this.reader.close();
            this.clientSocket.close();
            this.writer = null;
            this.reader = null;
            this.clientSocket = null;
        } catch (IOException e) {
            this.printError("Client failed to close socket or read/write streams!", e);
        }
    }

    public boolean isConnected() {
        return this.clientSocket != null && this.clientSocket.isConnected();
    }

    public DataInputStream getSocketInputStream() {
        try {
            return new DataInputStream(this.clientSocket.getInputStream());
        } catch (IOException e) {
            Logger.getInstance().logError("Failed to get socket input stream", e);
        }

        return null;
    }

    public String getMapFileName() {
        return this.mapFileName;
    }

    private void receiveFilesFromServer(Socket serverSocket) throws IOException {
        DataInputStream dis = new DataInputStream(serverSocket.getInputStream());

        this.mapFileName = dis.readUTF();
        final byte expectedFilesCount = dis.readByte();

        for (int i = 0; i < expectedFilesCount; ++i) {
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();

            FileOutputStream fos = new FileOutputStream(fileName);
            byte[] buffer = new byte[8 * 1024];
            int readBytes;
            while (fileSize > 0 && (readBytes = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                fos.write(buffer, 0, readBytes);
                fileSize -= readBytes;
            }

            fos.close();
        }

        System.out.println("Map files received successfully!");
    }

    private void printError(String message, Exception error) {
        System.out.println(message);
        if (error != null) {
            Logger.getInstance().logError(message, error);
        }
    }
}