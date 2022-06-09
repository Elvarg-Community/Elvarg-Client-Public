import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven(url = "https://raw.githubusercontent.com/open-osrs/hosting/master")
}

dependencies {
    implementation(gradleApi())
    implementation(group = "net.runelite", name = "fernflower", version = "07082019")
    implementation(group = "org.json", name = "json", version = "20190722")
    implementation("software.amazon.awssdk:s3:2.17.207")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

}

