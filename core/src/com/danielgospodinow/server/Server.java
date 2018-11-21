package com.danielgospodinow.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private HashMap<String, Socket> clients = new HashMap<String, Socket>();

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(8888);
        Socket client = serverSocket.accept();

        while(true) {
            Thread.sleep(1000);
            PrintStream ps = new PrintStream(client.getOutputStream(), true);
            ps.println("Opi  kurwa");
        }
    }
}
