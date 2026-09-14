import org.gradle.internal.os.OperatingSystem

plugins {
    java
    `maven-publish`
    signing
    id("io.github.sgtsilvio.gradle.maven-central-publishing") version "0.5.0"
}

group = "io.github.ImpulseStory"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withJavadocJar()
    withSourcesJar()
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

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-assimp")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    implementation("org.apache.commons:commons-csv:1.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-assimp:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-openal:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:$lwjglNatives")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    main {
        java {
            exclude("org/examples/**")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "libGML4J"
        attributes["Implementation-Version"] = project.version
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("libGML4J")
                description.set("A lightweight 2D game toolkit for Java")
                url.set("https://github.com/ImPulseStory/libGML4J")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("ImPulseStory")
                        name.set("ImPulse")
                    }
                }
                scm {
                    url.set("https://github.com/ImPulseStory/libGML4J")
                }
            }
        }
    }
}

signing {
    isRequired = project.hasProperty("signing.keyId")
    sign(publishing.publications["maven"])
}