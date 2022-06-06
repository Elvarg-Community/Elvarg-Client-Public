group = "com.runescape"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.runelite.net")
}

plugins {
    id("java")
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.lombok") version "1.5.21"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.22")
    annotationProcessor(group = "org.pf4j", name = "pf4j", version = "3.6.0")

    compileOnly(group = "javax.annotation", name = "javax.annotation-api", version = "1.3.2")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.22")
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
    implementation(group = "com.h2database", name = "h2", version = "1.4.200")
    implementation(group = "com.jakewharton.rxrelay3", name = "rxrelay", version = "3.0.1")
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.1")
    implementation(group = "io.reactivex.rxjava3", name = "rxjava", version = "3.1.2")
    implementation(group = "org.jgroups", name = "jgroups", version = "5.1.9.Final")
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
    implementation(group = "org.pf4j", name = "pf4j-update", version = "2.3.0")
    implementation(group = "net.runelite.gluegen", name = "gluegen-rt", version = "2.4.0-rc-20200429")
    implementation(group = "net.runelite.jogl", name = "jogl-all", version = "2.4.0-rc-20200429")
    implementation(group = "net.runelite.jocl", name = "jocl", version = "1.0")
    implementation(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
    implementation(group = "com.google.inject", name = "guice", version = "5.0.1")
    implementation(group = "com.fifesoft", name = "rsyntaxtextarea", version = "3.1.2")
    implementation(group = "com.fifesoft", name = "autocomplete", version = "3.1.1")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
    implementation(group = "net.runelite.pushingpixels", name = "trident", version = "1.5.00")
    implementation("net.runelite.jogl:jogl-all:2.4.0-rc-20200429")
    implementation("net.runelite.jocl:jocl:1.0")
    implementation("net.runelite.gluegen:gluegen-rt:2.4.0-rc-20200429")

    implementation("net.runelite.gluegen:gluegen-rt:2.4.0-rc-20200429:natives-windows-amd64")
    implementation("net.runelite.gluegen:gluegen-rt:2.4.0-rc-20200429:natives-windows-i586")
    implementation("net.runelite.gluegen:gluegen-rt:2.4.0-rc-20200429:natives-linux-amd64")

    implementation("net.runelite.jogl:jogl-all:2.4.0-rc-20200429:natives-windows-amd64")
    implementation("net.runelite.jogl:jogl-all:2.4.0-rc-20200429:natives-windows-i586")
    implementation("net.runelite.jogl:jogl-all:2.4.0-rc-20200429:natives-linux-amd64")

}


java {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
}

