package com.danielgospodinow.riggster.server.utils;

import com.danielgospodinow.riggster.server.Server;
import com.danielgospodinow.riggster.server.gameobjects.Enemy;
import com.danielgospodinow.riggster.server.gameobjects.Treasure;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MapLoader {

    public static List<Integer> loadTreasures() {
        String mapFilepath = String.format("%s/%s.%s", Server.MAP_DIRECTORY, Server.MAP_NAME, Server.MAP_MAIN_EXTENSION);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(mapFilepath))))) {
            LinkedList<Integer> treasures = new LinkedList<>();
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

                    int id = Integer.parseInt(values.remove(0));
                    treasures.push(id);
                }
            }

            return treasures;
        } catch (IOException e) {
            Logger.getInstance().logError("Failed to load treasures from map!", e);
            return null;
        }
    }

    public static List<Enemy> loadEnemies() {
        String mapFilepath = String.format("%s/%s_enemies.txt", Server.MAP_DIRECTORY,
                Server.MAP_NAME);
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(mapFilepath))))) {
            List<Enemy> enemies = new ArrayList<>();

            String line;
            String[] enemyArgs;
            while((line = reader.readLine()) != null) {
                enemyArgs = line.split(" ");
                int id = Integer.parseInt(enemyArgs[0]);
                int x = Integer.parseInt(enemyArgs[1]);
                int y = Integer.parseInt(enemyArgs[2]);
                int health = Integer.parseInt(enemyArgs[3]);
                int damage = Integer.parseInt(enemyArgs[4]);

                enemies.add(new Enemy(id, x, y, health, damage));
            }

            return enemies;
        } catch (IOException e) {
            Logger.getInstance().logError("Failed to load enemies from map!", e);
            return null;
        }
    }
}
