import org.apache.tools.ant.filters.ReplaceTokens
import java.text.SimpleDateFormat
import java.util.Date
import org.ajoberstar.grgit.Grgit

group = "com.runescape"
version = Project.clientVersion


repositories {
    mavenCentral()
    maven("https://repo.runelite.net")
}

val localGitCommit: String = try {
    val projectPath = rootProject.projectDir.absolutePath
    Grgit.open(mapOf("dir" to projectPath)).head().id
} catch (_: Exception) {
    "n/a"
}

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.ajoberstar.grgit") version "4.1.0"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.lombok") version "1.5.21"
    application
}


apply<BootstrapPlugin>()

dependencies {

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = Project.lombokVersion)
    annotationProcessor(group = "org.pf4j", name = "pf4j", version = "3.6.0")

    compileOnly(group = "javax.annotation", name = "javax.annotation-api", version = "1.3.2")
    compileOnly(group = "org.projectlombok", name = "lombok", version = Project.lombokVersion)
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
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
    implementation(group = "org.pf4j", name = "pf4j", version = "3.6.0") {
        exclude(group = "org.slf4j")
    }

    implementation(group = "net.runelite.jogl", name = "jogl-rl", version = "2.4.0-rc-20220318")
    implementation(group = "net.runelite.jogl", name = "jogl-gldesktop-dbg", version = "2.4.0-rc-20220318")
    implementation(group = "net.runelite.jocl", name = "jocl", version = "1.0")

    runtimeOnly(group = "net.runelite.pushingpixels", name = "trident", version = "1.5.00")

    testAnnotationProcessor(group = "org.projectlombok", name = "lombok", version = Project.lombokVersion)

    testCompileOnly(group = "org.projectlombok", name = "lombok", version = Project.lombokVersion)

    testImplementation(group = "com.google.inject.extensions", name = "guice-grapher", version = "4.1.0")
    testImplementation(group = "com.google.inject.extensions", name = "guice-testlib", version = "4.1.0")
    testImplementation(group = "org.hamcrest", name = "hamcrest-library", version = "1.3")
    testImplementation(group = "junit", name = "junit", version = "4.13.1")
    testImplementation(group = "org.mockito", name = "mockito-core", version = "3.1.0")
    testImplementation(group = "org.mockito", name = "mockito-inline", version = "3.1.0")
    testImplementation(group = "com.squareup.okhttp3", name = "mockwebserver", version = "4.9.1")
    testImplementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
    implementation("io.sentry:sentry-logback:6.0.0")

    implementation(platform("org.lwjgl:lwjgl-bom:3.3.1"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("net.runelite:rlawt:1.3")

    listOf("linux", "macos", "macos-arm64", "windows-x86", "windows").forEach {
        runtimeOnly("org.lwjgl:lwjgl::natives-$it")
        runtimeOnly("org.lwjgl:lwjgl-opengl::natives-$it")
    }

}

fun pluginPath(): String {
    if (project.hasProperty("pluginPath")) {
        return project.property("pluginPath").toString()
    }
    return ""
}

fun formatDate(date: Date?) = with(date ?: Date()) {
    SimpleDateFormat("MM-dd-yyyy").format(this)
}

tasks {

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
        dirMode = 493
        fileMode = 420
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        val tokens = mapOf(
            "client.osrs.version" to Project.clientVersion,
            "client.osrs.builddate" to formatDate(Date()),
            "plugin.path" to pluginPath()
        )

        inputs.properties(tokens)

        filesMatching("**/*.properties") {
            filter(ReplaceTokens::class, "tokens" to tokens)
            filteringCharset = "UTF-8"
        }
    }

    jar {
        manifest {
            attributes(mutableMapOf("Main-Class" to "net.runelite.client.RuneLite"))
        }
    }

    withType<BootstrapTask> {
        group = "rsps"

    }

    project.extra["gitCommit"] = localGitCommit
    project.extra["rootPath"] = rootDir.toString().replace("\\", "/")

    register<JavaExec>("RuneLite.main()") {
        group = "rsps"

        classpath = project.sourceSets.main.get().runtimeClasspath
        enableAssertions = true
        mainClass.set("net.runelite.client.RuneLite")
    }

}

application {
    mainClass.set("net.runelite.client.RuneLite")
}
