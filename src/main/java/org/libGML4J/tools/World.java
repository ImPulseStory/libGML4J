package org.libGML4J.tools;

/*
 * Copyright (c) 2026 ImPulseStory
 * ... (полный текст MIT)
 */

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.libGML4J.Exceptions.MapLoadException;
import org.libGML4J.constants.Keys;
import org.libGML4J.core.Window;
import org.libGML4J.graphics.Sprite;
import org.libGML4J.graphics.SpriteBatch;
import org.libGML4J.input.Keyboard;
import org.libGML4J.utils.Rect;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages tile-based maps, including loading from CSV, rendering visible layers,
 * collision detection, and interactive tile handling.
 * <p>
 * A {@code World} works with a 2D integer grid where each number represents a
 * tile ID. It supports culling (only visible tiles are drawn) and simple
 * rectangular collision checks.
 *
 * @author ImPulseStory
 */
public class World {

    /**
     * Tile IDs that can be interacted with, mapped to the key code that triggers them.
     * <p>
     * The key is the tile ID as a string (e.g. {@code "1"}), the value is the
     * key code (e.g. {@code Keys.E}).
     */
    private Map<String, Integer> interactables = new HashMap<>();

    /**
     * Registers a tile as interactive.
     * <p>
     * When the player stands on a tile with the given ID and presses the
     * specified key, {@link #checkInteractables(Rect, int[][], int, Keyboard)}
     * will return {@code true}.
     *
     * @param tileId  the tile ID (as a string) that triggers the interaction
     * @param KeyCode the key code that activates the interaction
     */
    public void addInteractable(String tileId, int KeyCode) {
        interactables.put(tileId, KeyCode);
    }

    /**
     * Checks whether the player is standing on an interactive tile and is
     * pressing the corresponding trigger key.
     *
     * @param playerRect the player's bounding rectangle
     * @param map        the tile map as a 2D int array
     * @param tileSize   the size of one tile in pixels
     * @param keyboard   the keyboard instance to check key states
     * @return {@code true} if the player is on an interactive tile and the
     *         trigger key is pressed, {@code false} otherwise
     */
    public boolean checkInteractables(Rect playerRect, int[][] map, int tileSize, Keyboard keyboard) {
        if (map == null) {
            return false;
        }

        int col = playerRect.getCenterX() / tileSize;
        int row = playerRect.getCenterY() / tileSize;

        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length) {
            return false;
        }

        String tileId = String.valueOf(map[row][col]);
        if (interactables.containsKey(tileId)) {
            int trigger = interactables.get(tileId);
            if (keyboard.isPressed(trigger)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the player is standing on a specific tile.
     *
     * @param rect        the player's bounding rectangle
     * @param tileW       the width of one tile in pixels
     * @param tileH       the height of one tile in pixels
     * @param map         the tile map as a 2D int array
     * @param triggerTile the tile ID to check for
     * @return {@code true} if the player's center is on the given tile,
     *         {@code false} otherwise
     */
    public boolean triggerTo(Rect rect, int tileW, int tileH, int[][] map, int triggerTile) {
        if (map == null) {
            return false;
        }

        int col = rect.getCenterX() / tileW;
        int row = rect.getCenterY() / tileH;

        int rows = map.length;
        int cols = (rows > 0) ? map[0].length : 0;

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        int tileId = map[row][col];
        return tileId == triggerTile;
    }

    /**
     * Loads a tile map from a CSV file.
     * <p>
     * The CSV file must contain integers separated by commas, one row per line.
     *
     * @param filename the path to the CSV file
     * @return a 2D int array representing the map
     * @throws MapLoadException if the file cannot be read or parsed
     */
    public int[][] loadCSV(String filename) {
        try {
            return loadCSVtoIntMatrix(filename);
        } catch (Exception e) {
            throw new MapLoadException("Failed to load " + filename);
        }
    }

    /**
     * Parses a CSV file into a 2D int matrix.
     *
     * @param filename the path to the CSV file
     * @return a 2D int array representing the map
     * @throws RuntimeException if an I/O error occurs
     */
    public int[][] loadCSVtoIntMatrix(String filename) {
        List<int[]> tempLines = new ArrayList<>();

        try (Reader reader = new FileReader(filename)) {
            CSVParser parser = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : parser) {
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
        throw new MapLoadException("Failed to load " + filename);
    }

        return tempLines.toArray(new int[0][]);
    }

    /**
     * Renders a tile layer with culling.
     * <p>
     * Only tiles visible within the camera's viewport are drawn.
     *
     * @param map    the tile map as a 2D int array
     * @param atlas  a map of tile IDs to sprite instances
     * @param w      the width of one tile in pixels
     * @param h      the height of one tile in pixels
     * @param camera the camera used to determine visible area
     * @param batch  the sprite batch used for drawing
     */
    public void drawLayer(int[][] map, Map<Integer, Sprite> atlas, int w, int h, Camera camera, SpriteBatch batch) {
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
                    drawTile(tile_id, scX, scY, atlas, batch);
                }
            }
        }
    }

    /**
     * Draws a single tile at the given screen coordinates.
     *
     * @param tile_id the tile ID to draw
     * @param scX     the screen x-coordinate
     * @param scY     the screen y-coordinate
     * @param atlas   a map of tile IDs to sprite instances
     * @param batch   the sprite batch used for drawing
     */
    public void drawTile(int tile_id, float scX, float scY, Map<Integer, Sprite> atlas, SpriteBatch batch) {
        Sprite sprite = atlas.get(tile_id);
        if (sprite != null) {
            sprite.setPosition(scX, scY);
            batch.draw(sprite);
        }
    }

    /**
     * Checks whether a rectangle can move to a new position without colliding
     * with any blocked tiles.
     * <p>
     * The method checks all four corners of the rectangle against the map's
     * blocked tile IDs.
     *
     * @param rect         the rectangle to move
     * @param tileSize     the size of one tile in pixels
     * @param blockedTiles an array of tile IDs that are considered obstacles
     * @param map          the tile map as a 2D int array
     * @return {@code true} if the move is valid, {@code false} if it would
     *         result in a collision
     */
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