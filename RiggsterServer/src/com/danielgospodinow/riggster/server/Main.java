package com.danielgospodinow.riggster.server;

public class Main {

    private static int PORT = 3000;

    public static void main(String[] args) {
        System.out.println(String.format("Riggster server starting on port: %d", PORT));
        Server.getInstance().startServer(PORT);
    }
}
