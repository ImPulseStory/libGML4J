📄 README.md
markdown

# libGML4J

**A lightweight 2D game toolkit for Java, built on LWJGL 3 and OpenGL.**

`libGML4J` gives you the tools — window, input, rendering, audio, UI — without forcing you into a framework. Use everything, use part of it, or ignore it and write your own OpenGL. Your choice.

---

## ✨ Features

- **Window & Loop** — GLFW window, VSync, event polling, cleanup
- **Delta Time & FPS** — frame limiter, FPS counter
- **Input** — keyboard (pressed / just pressed / just released) and mouse (position, buttons, scroll, drag)
- **Primitives** — rectangle, square, triangle, circle, line
- **Sprites & Textures** — load PNG/JPG, UV slicing, flip, rotate, scale
- **Sprite Batching** — one draw call for many sprites
- **Fonts** — TrueType via STB, glyph atlas, text drawing through the batch
- **Audio** — OpenAL sound loading and playback
- **World** — CSV tilemaps, culling, collisions, interactable tiles
- **Camera** — follow, apply offsets
- **Rect** — integer rectangle math and collision checks
- **Exceptions** — clear, typed errors (`TextureLoadException`, `ShaderCompilationException`, …)
- **Zero dependencies** beyond LWJGL 3 and Apache Commons CSV

---

markdown

## 🚀 Installation

### 📦 Gradle (Kotlin DSL)

**JitPack** — *recommended for now*

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.ImPulseStory:libGML4J:v0.1.0")
}

Maven Central — coming soon
kotlin

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.ImPulseStory:libgml4j:0.1.0")
}

📦 Gradle (Groovy DSL)

JitPack — recommended for now
groovy

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ImPulseStory:libGML4J:v0.1.0'
}

Maven Central — coming soon
groovy

repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.ImPulseStory:libgml4j:0.1.0'
}

📦 Maven

JitPack — recommended for now
xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.ImPulseStory</groupId>
    <artifactId>libGML4J</artifactId>
    <version>v0.1.0</version>
</dependency>

Maven Central — coming soon
xml

<dependency>
    <groupId>io.github.ImPulseStory</groupId>
    <artifactId>libgml4j</artifactId>
    <version>0.1.0</version>
</dependency>
```
    ⚠️ Note: libGML4J is not yet published on Maven Central. Until then, please use the JitPack option. This section will be updated after the first Central release.
⚡ Quick Start
java

import org.libGML4J.core.Window;
import org.libGML4J.graphics.Sprite;
import org.libGML4J.graphics.SpriteBatch;

public class Main {
    public static void main(String[] args) {
        Window.createWindow(800, 600, "libGML4J Demo");

        SpriteBatch batch = new SpriteBatch();
        int tex = Sprite.loadTexture("player.png");
        Sprite player = new Sprite(tex, 100, 100, 32, 32);

        while (!Window.shouldClose()) {
            Window.clear(30, 30, 40);

            batch.begin();
            batch.draw(player);
            batch.end();

            Window.update();
        }

        Window.destroy();
    }
}

🧠 Philosophy

libGML4J is not a framework. It does not tell you how to structure your game. It is a toolkit — a set of independent, well-documented tools that you can combine however you want.

    Need only Window and your own OpenGL? Fine.

    Need SpriteBatch and nothing else? Fine.

    Need everything? Fine.

No ApplicationListener. No forced architecture. Just tools.
📚 Documentation

Full API documentation is available in DOCS.md.
📦 Modules
Package	Purpose
core	Window, Clock, Audio, Time
graphics	Sprite, SpriteBatch, Font, Glyph, Draw
input	Keyboard, Mouse
gl	Mesh, Shader
tools	World, Camera
utils	Rect
constants	Keys
Exceptions	Typed exceptions
🛠️ Requirements

    Java 21+

    LWJGL 3.4.3

    Apache Commons CSV 1.12.0

    OpenGL 3.3 capable GPU

📜 License

MIT License. See LICENSE for details.
🔗 Links

[![](https://jitpack.io/v/ImPulseStory/libGML4J.svg)](https://jitpack.io/#ImPulseStory/libGML4J)

    GitHub: github.com/ImPulseStory/libGML4J

    Maven Central: coming soon
