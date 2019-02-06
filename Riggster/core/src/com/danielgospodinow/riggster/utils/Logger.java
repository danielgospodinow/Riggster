package com.danielgospodinow.riggster.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final Logger instance = new Logger();

    public static Logger getInstance() {
        return instance;
    }

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private PrintWriter writer;

    private Logger() {
        try {
            this.writer = new PrintWriter(new FileOutputStream(new File("logs.txt"), true));
        } catch (FileNotFoundException e) {
            System.out.println("Failed to initialize log writer!");
            e.printStackTrace();
        }
    }

    public synchronized void write(String message) {
        this.writer.println(String.format("[%s]: %s", dateTimeFormatter.format(LocalDateTime.now()), message));
        this.writer.flush();
    }

    public synchronized void logError(String errorMessage, Exception exception) {
        this.write(errorMessage);
        exception.printStackTrace(this.writer);
    }
}
