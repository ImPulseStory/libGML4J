package org.libGML4J.tools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.libGML4J.core.Window;
import org.libGML4J.graphics.Sprite;
import org.libGML4J.input.Keyboard;
import org.libGML4J.utils.Rect;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class World {
    public int[] interatables;
    public Keyboard keyboard;

    public int[][] loadCSV(String filename) {
        try {
            int[][] matrix = loadCSVtoIntMatrix(filename);
            return matrix;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int[][] loadCSVtoIntMatrix(String filename) {
        List<int[]> tempLines = new ArrayList<>();

        try (Reader reader = new FileReader(filename)) {
            CSVParser parser = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : parser)  {
                int size = record.size();
                int[] temp = new int[size];
                for (int i = 0; i < size; i++) {
                    temp[i] = Integer.parseInt(record.get(i));
                }
                tempLines.add(temp);
                }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempLines.toArray(new int[0][]);
    }

    public void drawLayer(int[][] map, Map<Integer, Sprite> atlas, int w, int h, Camera camera) {
        int rows = map.length;
        int cols = (rows > 0) ? map[0].length : 0;

        int startX = Math.max(0, camera.getX() / w);
        int startY = Math.max(0, camera.getY() / h);
        int endX = Math.min(cols, (camera.getX() + Window.getWidth()) / w + 1);
        int endY = Math.min(rows, (camera.getY() + Window.getHeight()) / h + 1);

        for (int row = startY; row < endY; row++) {
            for (int col = startX; col < endX; col++) {
                int tile_id = map[row][col];
                if (atlas.containsKey(tile_id)) {
                    float scX = (col * w) - camera.getX();
                    float scY = (row * h) - camera.getY();
                    drawTile(tile_id, scX, scY, atlas);
                }
            }
        }
    }

    public void drawTile(int tile_id, float scX, float scY, Map<Integer, Sprite> atlas) {
        Sprite sprite = atlas.get(tile_id);
        if (sprite != null) {
            sprite.setPosition(scX, scY);
            sprite.draw();
        }
    }

    public boolean canMoveTo(Rect rect, int tileSize, int[] blockedTiles, int[][] map) {
        int[][] corners = {
                { rect.getLeft(),  rect.getTop() },
                { rect.getRight() - 1, rect.getTop() },
                { rect.getLeft(),  rect.getBottom() - 1 },
                { rect.getRight() - 1, rect.getBottom() - 1 }
        };

        for (int[] corner : corners) {
            int cx = corner[0];
            int cy = corner[1];
            int mx = cx / tileSize;
            int my = cy / tileSize;

            if (my < 0 || my >= map.length || mx < 0 || mx >= map[0].length) {
                return false;
            }

            for (int blocked : blockedTiles) {
                if (map[my][mx] == blocked) {
                    return false;
                }
            }
        }
        return true;
    }
}
