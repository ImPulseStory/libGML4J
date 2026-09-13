package org.libGML4J;

import org.libGML4J.core.Audio;
import org.libGML4J.core.Clock;
import org.libGML4J.core.Window;
import org.libGML4J.graphics.Draw;
import org.libGML4J.graphics.Sprite;
import org.libGML4J.input.Keyboard;
import org.libGML4J.tools.Camera;
import org.libGML4J.tools.World;
import org.libGML4J.utils.Rect;

import java.util.Map;

import static org.libGML4J.constants.Keys.*;

public class Main {
    private static long window;

    public static void init() {
        window = Window.createWindow(800, 600, "penis");
        Keyboard.init(window);
        Draw.init();
        Audio.init();
    }

    public static void main(String[] args) {
        init();

        int speed = 10;
        Rect rect = new Rect(0, 0, 32, 32);
        int tex = Sprite.loadTexture("src/main/java/org/example/player_test.png");
        int[] blocked_tiles = {-1};
        Sprite player = new Sprite(tex, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        Map<Integer, Sprite> atlas = Sprite.cutTileSet("assets/tileset.png", 16, 16);
        atlas = Sprite.resizeTileSet(atlas, 32, 32);
        Camera camera = new Camera();
        World world = new World();

        int shot = Audio.loadSound("/home/impulsestory/Java/libGML/assets/farts-4-vorbis.ogg");
        Audio.setVolume(shot, 0.5f);
        Audio.setLooping(shot, true);
        Audio.play(shot);

        int[][] matrix = world.loadCSV("assets/map.csv");

        while (!Window.shouldClose()) {
            if (Keyboard.isPressed(W)) {
                int dy = 0;
                dy -= speed;
                Rect newRect = rect.move(0, dy, rect);
                if (world.canMoveTo(newRect, 32, blocked_tiles, matrix)) {
                    rect = newRect;
                }
            }
            if (Keyboard.isPressed(A)) {
                int dx = 0;
                dx -= speed;
                Rect newRect = rect.move(dx, 0, rect);
                if (world.canMoveTo(newRect, 32, blocked_tiles, matrix)) {
                    rect = newRect;
                }
            }
            if (Keyboard.isPressed(S)) {
                int dy = 0;
                dy += speed;
                Rect newRect = rect.move(0, dy, rect);
                if (world.canMoveTo(newRect, 32, blocked_tiles, matrix)) {
                    rect = newRect;
                }
            }
            if (Keyboard.isPressed(D)) {
                int dx = 0;
                dx += speed;
                Rect newRect = rect.move(dx, 0, rect);
                if (world.canMoveTo(newRect, 32, blocked_tiles, matrix)) {
                    rect = newRect;
                }
            }

            Window.clear(20, 20, 30);

            camera.follow(rect);
            world.drawLayer(matrix, atlas, 32, 32, camera);

            player.setPosition(camera.applyX(rect.getX()), camera.applyY(rect.getY()));
            player.draw();

            Clock.setTargetFPS(60);
            Window.update();
        }
        Audio.destroy();
        Window.destroy();
    }
}