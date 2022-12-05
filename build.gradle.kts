import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.runescape"
version = 1.0

repositories {
    mavenCentral()
    maven("https://repo.runelite.net")
}

plugins {
    kotlin("jvm")
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

javafx {
    modules("javafx.base")
}

dependencies {
    val lombokVersion = "1.18.24"
    val slf4jVersion = "1.7.36"
    val lwjglVersion = "3.3.1"
    val lwjglClassifiers = arrayOf(
        "natives-linux",
        "natives-windows-x86", "natives-windows",
        "natives-macos", "natives-macos-arm64"
    )
    val joglVersion = "2.4.0-rc-20220318"
    val joglClassifiers = arrayOf(
        "natives-linux-amd64",
        "natives-windows-amd64", "natives-windows-i586",
        "natives-macosx-universal"
    )

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)

    compileOnly(group = "javax.annotation", name = "javax.annotation-api", version = "1.3.2")
    compileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
    compileOnly(group = "net.runelite", name = "orange-extensions", version = "1.0")

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.9")
    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.5")
    implementation(group = "com.google.guava", name = "guava", version = "30.1.1-jre") {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
        exclude(group = "com.google.errorprone", module = "error_prone_annotations")
        exclude(group = "com.google.j2objc", module = "j2objc-annotations")
        exclude(group = "org.codehaus.mojo", module = "animal-sniffer-annotations")
    }
    implementation(group = "com.google.inject", name = "guice", version = "5.0.1")
    implementation(group = "com.google.protobuf", name = "protobuf-javalite", version = "3.21.1")
    implementation(group = "com.jakewharton.rxrelay3", name = "rxrelay", version = "3.0.1")
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.1")
    implementation(group = "io.reactivex.rxjava3", name = "rxjava", version = "3.1.2")
    implementation(group = "org.jgroups", name = "jgroups", version = "5.2.2.Final")
    implementation(group = "net.java.dev.jna", name = "jna", version = "5.9.0")
    implementation(group = "net.java.dev.jna", name = "jna-platform", version = "5.9.0")
    implementation(group = "net.runelite", name = "discord", version = "1.4")
    implementation(group = "net.runelite.pushingpixels", name = "substance", version = "8.0.02")
    implementation(group = "net.sf.jopt-simple", name = "jopt-simple", version = "5.0.4")
    implementation(group = "org.madlonkay", name = "desktopsupport", version = "0.6.0")
    implementation(group = "org.apache.commons", name = "commons-text", version = "1.9")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.9.0")
    implementation(group = "commons-io", name = "commons-io", version = "2.8.0")
    implementation(group = "org.jetbrains", name = "annotations", version = "22.0.0")
    implementation(group = "com.github.zafarkhaja", name = "java-semver", version = "0.9.0")
    implementation(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)
    implementation("com.beust:klaxon:5.5")
    // implementation(group = "com.google.archivepatcher", name = "archive-patch-applier", version= "1.0.4")

    implementation(group = "net.runelite.gluegen", name = "gluegen-rt", version = joglVersion)
    implementation(group = "net.runelite.jogl", name = "jogl-rl", version = joglVersion)
    implementation(group = "net.runelite.jogl", name = "jogl-gldesktop-dbg", version = joglVersion)
    implementation(group = "net.runelite.jocl", name = "jocl", version = "1.0")

    implementation(group = "net.runelite", name = "rlawt", version = "1.3")

    implementation(group = "org.lwjgl", name = "lwjgl", version = lwjglVersion)
    implementation(group = "org.lwjgl", name = "lwjgl-opengl", version = lwjglVersion)
    for (classifier in lwjglClassifiers) {
        implementation(group = "org.lwjgl", name = "lwjgl", version = lwjglVersion, classifier = classifier)
        implementation(group = "org.lwjgl", name = "lwjgl-opengl", version = lwjglVersion, classifier = classifier)
    }

    runtimeOnly(group = "net.runelite.pushingpixels", name = "trident", version = "1.5.00")

    for (classifier in joglClassifiers) {
        runtimeOnly(group = "net.runelite.jogl", name = "jogl-rl", version = joglVersion, classifier = classifier)
        runtimeOnly(group = "net.runelite.gluegen", name = "gluegen-rt", version = joglVersion, classifier = classifier)
    }

    runtimeOnly(group = "net.runelite.jocl", name = "jocl", version = "1.0", classifier = "macos-x64")
    runtimeOnly(group = "net.runelite.jocl", name = "jocl", version = "1.0", classifier = "macos-arm64")

    testAnnotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)
    testCompileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)

    testImplementation(group = "com.google.inject.extensions", name = "guice-grapher", version = "4.1.0")
    testImplementation(group = "com.google.inject.extensions", name = "guice-testlib", version = "4.1.0")
    testImplementation(group = "org.hamcrest", name = "hamcrest-library", version = "1.3")
    testImplementation(group = "junit", name = "junit", version = "4.12")
    testImplementation(group = "org.mockito", name = "mockito-core", version = "3.1.0")
    testImplementation(group = "org.mockito", name = "mockito-inline", version = "3.1.0")
    testImplementation(group = "com.squareup.okhttp3", name = "mockwebserver", version = "4.9.1")
    testImplementation(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)
}

application {
    mainClass.set("net.runelite.client.RuneLite")
}

tasks.withType<JavaCompile>().configureEach {
    options.isWarnings = false
    options.isDeprecation = false
    options.isIncremental = true
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
