plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.gradle.userdev") version "7.1.38"
    id("net.neoforged.licenser") version "0.7.2"
}

// ── Mod metadata ───────────────────────────────────────────────────────────────

val modId: String = "examplemod"
val modVersion: String = System.getenv("VERSION") ?: "0.0.0-indev"
val modName: String = "Example Mod"
val modGroupId: String = "com.example.examplemod"

val minecraftVersion: String = "1.21.1"
val neoforgeVersion: String = "21.1.234"

val modDataOutput = "src/generated/resources"

version = modVersion
group = modGroupId

base {
    archivesName = modId
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

// ── Source sets ───────────────────────────────────────────────────────────────

val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "modId" to modId,
        "modVersion" to modVersion,
        "modName" to modName,
        "minecraftVersionRange" to "[$minecraftVersion]",
        "neoforgeVersionRange" to "[$neoforgeVersion,)",
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

sourceSets {
    main {
        resources {
            srcDir(modDataOutput)
            srcDir(generateModMetadata)
        }
    }
    create("data")
}

// ── Repositories ──────────────────────────────────────────────────────────────

repositories {
    mavenCentral()
}

// ── NeoGradle run configs ─────────────────────────────────────────────────────

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")
        workingDirectory = project.layout.projectDirectory.dir("run").dir(name)
        modSource(project.sourceSets.main.get())
    }
    register("client") {
        systemProperty("neoforge.enabledGameTestNamespaces", modId)
    }
    register("server") {
        systemProperty("neoforge.enabledGameTestNamespaces", modId)
        argument("--nogui")
    }
    register("gameTestServer") {
        systemProperty("neoforge.enabledGameTestNamespaces", modId)
    }
    register("data") {
        modSource(sourceSets.getByName("data"))
        arguments(
            "--mod", modId,
            "--all",
            "--output", file(modDataOutput).absolutePath,
            "--existing", file("src/main/resources").absolutePath
        )
    }
}

// ── Dependencies ──────────────────────────────────────────────────────────────

configurations {
    val dataImplementation by getting {
        extendsFrom(configurations.implementation.get())
    }
}

dependencies {
    implementation("net.neoforged:neoforge:$neoforgeVersion")
    "dataImplementation"(sourceSets.main.get().output)

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
}

// ── License header enforcement ────────────────────────────────────────────────

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
}

// ── Tasks ─────────────────────────────────────────────────────────────────────

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// ── Publishing ────────────────────────────────────────────────────────────────

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file://${rootProject.projectDir}/repo")
        }
    }
}
