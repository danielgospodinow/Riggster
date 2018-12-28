package com.danielgospodinow.riggster.server.utils;

import com.danielgospodinow.riggster.server.Server;
import com.danielgospodinow.riggster.server.gameobjects.Treasure;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MapLoader {

    public static List<Rectangle> loadTreasures() {
        try {
            String mapFilepath = String.format("%s/%s.%s", Server.getInstance().MAP_DIRECTORY,
                    Server.getInstance().MAP_NAME, Server.getInstance().MAP_MAIN_EXTENSION);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(new File(mapFilepath))));

            LinkedList<Rectangle> treasures = new LinkedList<>();
            boolean reachedTreasures = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("treasures")) {
                    reachedTreasures = true;
                    continue;
                }

                if (line.contains("/objectgroup") && reachedTreasures) {
                    break;
                }

                if (reachedTreasures) {
                    List<String> values = Arrays.stream(line.split("[^0-9.]"))
                            .filter(value -> !value.equals(""))
                            .collect(Collectors.toList());
                    values.remove(0);

                    int x = Math.round(Float.parseFloat(values.remove(0)));
                    int y = Math.round(Float.parseFloat(values.remove(0)));
                    int width = Math.round(Float.parseFloat(values.remove(0)));
                    int height = Math.round(Float.parseFloat(values.remove(0)));

                    treasures.push(new Rectangle(x, y, width, height));
                }
            }

            return treasures;
        } catch (IOException e) {
            System.out.println("Failed to load treasures from map!");
            e.printStackTrace();
            return null;
        }
    }
}
