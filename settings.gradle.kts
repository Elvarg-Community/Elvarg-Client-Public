rootProject.name = "Elvarg-Client-Coummnity"

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven("https://repo.runelite.net")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    plugins {
        kotlin("jvm") version "1.7.22"
        id("org.openjfx.javafxplugin") version "0.0.13"
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}
