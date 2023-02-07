import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.8.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

val projectName = "MinecraftPython"
group = "com.mcpy"
val ver = "1.0.0"
version = ver
val mcVersion = "1.19.3"

repositories {
    mavenCentral()
    maven(url="https://papermc.io/repo/repository/maven-public/")
    maven(url="https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    // JetBrains
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.eclipse.jdt:core:3.3.0-v_771")
    implementation("com.google.googlejavaformat:google-java-format:1.15.0")

    // Dependencies for the plugin
    compileOnly("io.papermc.paper:paper-api:$mcVersion-R0.1-SNAPSHOT")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

// For convenience
println(project.gradle.gradleUserHomeDir)
val folder = file(project.gradle.gradleUserHomeDir.path.dropLast(7) +  "\\Desktop\\Minecraft\\Paper 1.19 Server\\plugins")

tasks {
//    test {
//        useJUnitPlatform()
//    }

//    jar {
//        this.destinationDirectory.set(folder)
//    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("MinecraftPython.jar")
    destinationDirectory.set(folder)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

bukkit {
    main = "$group.lang.$projectName"
    name = projectName
    description = "A compiler!"
    version = ver
    authors = listOf("ZeEpic", "Hockus")
    apiVersion = mcVersion.substring(0, 4)
}
