# libGML4J — API Documentation

Complete reference for all public classes and methods.

---

## Table of Contents

1. [core.Window](#corewindow)
2. [core.Clock](#coreclock)
3. [core.Time](#coretime)
4. [core.Audio](#coreaudio)
5. [input.Keyboard](#inputkeyboard)
6. [input.Mouse](#inputmouse)
7. [graphics.Draw](#graphicsdraw)
8. [graphics.Sprite](#graphicssprite)
9. [graphics.SpriteBatch](#graphicsspritebatch)
10. [graphics.Font](#graphicsfont)
11. [graphics.Glyph](#graphicsglyph)
12. [gl.Mesh](#glmesh)
13. [gl.Shader](#glshader)
14. [tools.World](#toolsworld)
15. [tools.Camera](#toolscamera)
16. [utils.Rect](#utilsrect)
17. [constants.Keys](#constantskeys)
18. [Exceptions](#exceptions)

---

## core.Window

Manages the GLFW window and OpenGL context.

### `createWindow(int width, int height, String title)`

Creates a GLFW window, makes the OpenGL context current, enables VSync, blending, and returns the window handle.

long window = Window.createWindow(800, 600, "My Game");

update()

Polls events and swaps buffers. Call once per frame.
shouldClose()

Returns true if the window should close.
clear(float r, float g, float b) / clear(float r, float g, float b, float a)

Clears the window with the given color (0–255).
destroy(long window)

Destroys the window and terminates GLFW.
Other methods

    getWidth(), getHeight() — window size

    getTitle(), setTitle(String) — title

    getWindow() — GLFW handle

    setSize(int, int), setWidth(int), setHeight(int)

core.Clock

Delta time, FPS counter, frame limiter.
update()

Updates delta time and FPS. Call once at the start of every frame.
getDeltaTime()

Returns time between the last two frames, in seconds.
getFPS()

Returns the current FPS.
setTargetFPS(int) / getTargetFPS()

Sets/gets the target FPS. Use 0 for unlimited.
java

Clock.update();
float dt = Clock.getDeltaTime();
Clock.setTargetFPS(60);

core.Time

Monotonic time since application start.
getTime()

Returns elapsed time in seconds.
core.Audio

OpenAL sound loading and playback.
init()

Initializes OpenAL. Call once after window creation.
loadSound(String path)

Loads an OGG file and returns a source ID.
play(int sourceID) / pause(int) / stop(int)

Playback control.
setVolume(int sourceID, float volume) / setLooping(int, boolean)

Playback settings.
destroy()

Deletes all sources and buffers, closes OpenAL.
java

Audio.init();
int shot = Audio.loadSound("shot.ogg");
Audio.play(shot);

input.Keyboard

Static keyboard input.
init(long window)

Registers the GLFW key callback.
update()

Copies current state to previous. Call once per frame.
isPressed(int key)

Returns true if the key is currently held.
isJustPressed(int key)

Returns true only on the frame the key was pressed.
isJustReleased(int key)

Returns true only on the frame the key was released.
java

Keyboard.init(Window.getWindow());

while (!Window.shouldClose()) {
    Keyboard.update();
    if (Keyboard.isPressed(Keys.W)) { /* ... */ }
}

input.Mouse

Singleton mouse input.
get()

Returns the shared instance.
Callbacks

Register with GLFW:
java

glfwSetCursorPosCallback(window, Mouse::mousePosCallback);
glfwSetMouseButtonCallback(window, Mouse::mouseButtonCallback);
glfwSetScrollCallback(window, Mouse::mouseScrollCallback);

endFrame()

Resets per-frame values. Call once at the end of every frame.
Getters

    getX(), getY() — cursor position

    getDx(), getDy() — movement since last frame

    getScrollX(), getScrollY() — scroll offsets

    isDragging() — whether a button is held while moving

    mouseButtonDown(int button) — button state (0 = left, 1 = middle, 2 = right)

graphics.Draw

Static 2D primitives.
init()

Compiles shaders and prepares meshes. Call once after window creation.
rect(int x, int y, int w, int h, int r, int g, int b)

Draws a filled rectangle.
square(int x, int y, int size, int r, int g, int b)

Draws a square.
circle(float x, float y, float radius, int r, int g, int b)

Draws a filled circle.
triangle(float x1, float y1, float x2, float y2, float x3, float y3, int r, int g, int b)

Draws a filled triangle.
line(float x1, float y1, float x2, float y2, float thickness, int r, int g, int b)

Draws a line with thickness.
java

Draw.init();
Draw.rect(100, 100, 200, 150, 255, 0, 0);
Draw.circle(400, 300, 50, 0, 255, 0);

graphics.Sprite

A drawable 2D image.
Constructors
java

// Full texture
Sprite sprite = new Sprite(textureID, x, y, w, h);

// Tile (sub-region)
Sprite tile = new Sprite(textureID, u0, v0, u1, v1, w, h);

loadTexture(String path)

Loads a PNG/JPG and returns the texture ID.
cutTileSet(String path, int tileW, int tileH)

Slices a tileset into a Map<Integer, Sprite>.
resizeTileSet(Map<Integer, Sprite> set, int tileW, int tileH)

Returns a new tileset with the given tile size.
draw()

Draws the sprite immediately. For batching, use SpriteBatch instead.
Setters

    setPosition(float x, float y)

    rotate(float angle)

    flip(boolean flipX, boolean flipY)

    scale(float w, float h)

Getters

    getTextureID(), getX(), getY(), getW(), getH()

    getU0(), getV0(), getU1(), getV1()

graphics.SpriteBatch

Batches sprites into a single draw call.
Constructor
java

SpriteBatch batch = new SpriteBatch();

begin()

Starts a new batch.
draw(Sprite sprite)

Adds a sprite to the batch. Flushes automatically if the buffer is full or the texture changes.
end()

Draws all accumulated sprites.
flush()

Draws and resets the buffer without ending the batch.
java

batch.begin();
batch.draw(sprite1);
batch.draw(sprite2);
batch.end();

graphics.Font

TrueType font baked into a texture.
Constructor
java

Font font = new Font("arial.ttf", 24);

draw(String text, float x, float y, SpriteBatch batch)

Draws text at the given position.
destroy()

Deletes the font texture.
Getters

    getSize(), getFont(), getTextureID(), getGlyph(char c)

graphics.Glyph

Metrics for a single character.
Fields

    u0, v0, u1, v1 — UV coordinates

    w, h — size in pixels

    offsetX, offsetY — offset from cursor

    advance — cursor advance

gl.Mesh

VAO/VBO/EBO wrapper for indexed geometry.
Constructor
java

Mesh mesh = new Mesh(vertices, indices, hasTexCoords, float5);

    float5 = true — 5 floats per vertex (pos + UV)

    hasTexCoords = true — 9 floats per vertex (pos + color + UV)

    neither — 7 floats per vertex (pos + color)

draw()

Draws the mesh.
update(float[] vertices)

Updates vertex data without recreating the mesh.
destroy()

Deletes the mesh.
gl.Shader

OpenGL shader program.
Constructor
java

Shader shader = new Shader(vertexSource, fragmentSource);

Throws ShaderCompilationException on failure.
bind() / unbind()

Activates/deactivates the program.
destroy()

Deletes the program.
Uniform setters (static)

    setUniform1i(String, int)

    setUniform1f(String, float)

    setUniform2f(String, float, float)

    setUniform3f(String, float, float, float)

    setUniform4f(String, float, float, float, float)

    setUniformMatrix4(String, float[])

    setUniform2i(String, int, int)

    setUniform3i(String, int, int, int)

tools.World

Tilemap loading, rendering, collisions, interactables.
loadCSV(String filename)

Loads a CSV tilemap into int[][].
addInteractable(String tileId, int keyCode)

Registers a tile as interactive.
checkInteractables(Rect playerRect, int[][] map, int tileSize, Keyboard keyboard)

Returns true if the player is on an interactive tile and the trigger key is pressed.
triggerTo(Rect rect, int tileW, int tileH, int[][] map, int triggerTile)

Returns true if the player is on the given tile.
drawLayer(int[][] map, Map<Integer, Sprite> atlas, int w, int h, Camera camera, SpriteBatch batch)

Draws visible tiles with culling.
drawTile(int tileId, float scX, float scY, Map<Integer, Sprite> atlas, SpriteBatch batch)

Draws a single tile.
canMoveTo(Rect rect, int tileSize, int[] blockedTiles, int[][] map)

Returns true if the rectangle can move without colliding.
tools.Camera

2D camera offset.
follow(Rect rect)

Centers the camera on the rectangle.
applyX(int x) / applyY(int y)

Converts world coordinates to screen coordinates.
player(Rect rect)

Returns a copy of the rectangle in screen coordinates.
Getters

    getX(), getY()

utils.Rect

Integer rectangle with collision helpers.
Constructor
java

Rect rect = new Rect(x, y, width, height);

Getters

    getX(), getY(), getWidth(), getHeight()

    getLeft(), getRight(), getTop(), getBottom()

    getCenterX(), getCenterY()

intersects(Rect other)

Returns true if the rectangles overlap.
contains(int px, int py)

Returns true if the point is inside.
copy()

Returns a new copy.
move(int dx, int dy)

Moves in place.
move(int dx, int dy, Rect rect)

Returns a new moved rectangle.
setPosition(int x, int y) / setSize(int w, int h)

Sets position/size.
intersection(Rect other)

Returns the overlapping rectangle, or null.
constants.Keys

Key code constants.
Fields

    Letters: A … Z

    Digits: NUM_0 … NUM_9

    Function keys: F1 … F12

    Arrows: UP, DOWN, LEFT, RIGHT

    Modifiers: LEFT_SHIFT, RIGHT_SHIFT, LEFT_CONTROL, RIGHT_CONTROL, LEFT_ALT, RIGHT_ALT

    Others: SPACE, ENTER, ESCAPE, TAB, BACKSPACE, etc.

java

if (Keyboard.isPressed(Keys.W)) { /* ... */ }

Exceptions

All exceptions extend LibGMLException.
Exception	Thrown by
TextureLoadException	Sprite.cutTileSet, Sprite.loadTexture
ShaderCompilationException	Shader constructor
MapLoadException	World.loadCSV
SoundLoadException	Audio.loadSound
FontLoadException	Font constructor
java

try {
    Sprite.loadTexture("player.png");
} catch (TextureLoadException e) {
    System.err.println("Texture failed: " + e.getMessage());
}

License

MIT © ImPulseStory