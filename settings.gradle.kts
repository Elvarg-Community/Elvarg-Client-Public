rootProject.name = "Elvarg-Client-Coummnity"

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven("https://repo.runelite.net")
    }
}

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if(requested.id.toString() == "com.mark.bootstrap.bootstrap")
                useModule("com.github.Mark7625:bootstrap-release:-SNAPSHOT")
        }
    }
    repositories {
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    plugins {
        kotlin("jvm") version "1.7.22"
        id("org.openjfx.javafxplugin") version "0.0.13"
        id("com.github.johnrengelman.shadow") version "7.1.2"
        id("com.mark.bootstrap.bootstrap") version "-SNAPSHOT"
    }
}
