package com.danielgospodinow.riggster;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable {

    private List<String> messages = new ArrayList<String>();

    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public Client(SocketAddress address) {
        try {
            this.clientSocket = new Socket();
            this.clientSocket.connect(address);

            this.reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Client didn't manage to connect!");
        }
    }

    @Override
    public void run() {
        try {
            while(true) {
                this.messages.add(this.reader.readLine());
            }
        } catch (IOException e) {
            System.out.println("Client didn't manage to handle communication channels");
        }
    }

    public List<String> getMessages() {
        return this.messages;
    }
}
