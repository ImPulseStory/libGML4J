package org.examples;

import org.libGML4J.constants.Keys;
import org.libGML4J.core.Audio;
import org.libGML4J.core.Clock;
import org.libGML4J.core.Window;
import org.libGML4J.graphics.Draw;
import org.libGML4J.graphics.Font;
import org.libGML4J.graphics.Sprite;
import org.libGML4J.graphics.SpriteBatch;
import org.libGML4J.input.Keyboard;
import org.libGML4J.tools.Camera;
import org.libGML4J.tools.World;
import org.libGML4J.utils.Rect;

import java.io.IOException;
import java.util.Map;

import static org.libGML4J.constants.Keys.*;

public class MainExample {
    private static long window;
    private static SpriteBatch batch;
    private static World world = new World();
    private static Font font;
    private static Keyboard keyboard;
    private static Rect playerRect;
    private static Camera camera = new Camera();

    public static void init() throws IOException {
        window = Window.createWindow(800, 600, "libGML4J");
        batch = new SpriteBatch();
        font = new Font("assets/saira-thin.ttf", 24);
        playerRect = new Rect(0, 0, 32, 32);
        Keyboard.init(window);
        Draw.init();
        Audio.init();
    }

    public static void main(String[] args) throws IOException {
        init();

        int speed = 10;
        int tex = Sprite.loadTexture("src/main/java/org/libGML4J/player_test.png");
        int[] blocked_tiles = {-1};

        world.addInteractable("1", Keys.E);

        Sprite player = new Sprite(tex, playerRect.getX(), playerRect.getY(), playerRect.getWidth(), playerRect.getHeight());
        Map<Integer, Sprite> atlas = Sprite.cutTileSet("assets/tileset.png", 16, 16);
        atlas = Sprite.resizeTileSet(atlas, 32, 32);

        int[][] matrix = world.loadCSV("assets/map.csv");

        while (!Window.shouldClose()) {
            if (Keyboard.isPressed(W)) {
                int dy = 0;
                dy -= speed;
                Rect newRect = playerRect.move(0, dy, playerRect);
                if (world.canMoveTo(newRect, 32, blocked_tiles, matrix)) {
                    playerRect = newRect;
                }
            }
            if (Keyboard.isPressed(A)) {
                int dx = 0;
                dx -= speed;
                Rect newplayerRect = playerRect.move(dx, 0, playerRect);
                if (world.canMoveTo(newplayerRect, 32, blocked_tiles, matrix)) {
                    playerRect = newplayerRect;
                }
            }
            if (Keyboard.isPressed(S)) {
                int dy = 0;
                dy += speed;
                Rect newplayerRect = playerRect.move(0, dy, playerRect);
                if (world.canMoveTo(newplayerRect, 32, blocked_tiles, matrix)) {
                    playerRect = newplayerRect;
                }
            }
            if (Keyboard.isPressed(D)) {
                int dx = 0;
                dx += speed;
                Rect newplayerRect = playerRect.move(dx, 0, playerRect);
                if (world.canMoveTo(newplayerRect, 32, blocked_tiles, matrix)) {
                    playerRect = newplayerRect; }
            }
            if (world.checkInteractables(playerRect, matrix, 32, keyboard)) {
                System.out.println("Interact");
            }

            Window.clear(20, 20, 30);

            camera.follow(playerRect);
            batch.begin();
            world.drawLayer(matrix, atlas, 32, 32, camera, batch);
            batch.end();

            player.setPosition(camera.applyX(playerRect.getX()), camera.applyY(playerRect.getY()));
            player.draw();

            batch.begin();
            font.draw("FONT", 100, 100, batch);
            batch.end();

            Clock.setTargetFPS(60);
            Window.update();
        }
        Audio.destroy();
        Window.destroy();
    }
}