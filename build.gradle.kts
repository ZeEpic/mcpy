import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.rimlang"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    flatDir {
        dir("libs")
    }
}

val paperMc = "io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT"
dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation(files("libs/Formatter.jar"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    compileOnly(paperMc)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

val folder = file("C:\\Users\\isaol\\Desktop\\Paper 1.19 Server\\plugins")

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    this.destinationDirectory.set(folder)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("RimCompiler.jar")
    destinationDirectory.set(folder)
    dependencies {
        exclude(dependency(paperMc))
    }
}