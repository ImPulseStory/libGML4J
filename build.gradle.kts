import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
    `maven-publish`
}

group = "org.libGML"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainClass.set("org.example.Main")
}

repositories {
    mavenCentral()
}

// Detect OS and set natives
val lwjglVersion = "3.4.3"
val lwjglNatives: String by lazy {
    when (OperatingSystem.current()) {
        OperatingSystem.LINUX -> {
            val arch = System.getProperty("os.arch")
            when {
                arch.startsWith("arm") || arch.startsWith("aarch64") -> {
                    "natives-linux" + if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"
                }
                arch.startsWith("ppc") -> "natives-linux-ppc64le"
                arch.startsWith("riscv") -> "natives-linux-riscv64"
                else -> "natives-linux"
            }
        }
        OperatingSystem.WINDOWS -> "natives-windows"
        OperatingSystem.MAC_OS -> "natives-macos"
        else -> throw GradleException("Unsupported OS: ${OperatingSystem.current()}")
    }
}

tasks.named<JavaExec>("run") {
    environment("GDK_BACKEND", "x11")
}

dependencies {
    // LWJGL BOM for version management
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    // Core LWJGL modules
    implementation("org.lwjgl:lwjgl")
    implementation("org.apache.commons:commons-csv:1.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("org.lwjgl:lwjgl-assimp")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    // Native libraries (platform-specific)
    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-assimp:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-openal:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:$lwjglNatives")

    // Testing (optional, but good practice)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// JAR with manifest (optional, for executable JAR)
tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "libGML"
        attributes["Implementation-Version"] = project.version
    }
}