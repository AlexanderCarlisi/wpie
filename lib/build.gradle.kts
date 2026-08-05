plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    
    // WPILib Maven Repository for Command and Math libraries
    maven {
        url = uri("https://frcmaven.wpi.edu/artifactory/release")
    }
}

// Separate configuration for JNI native archives so Gradle can extract them
val nativeNatives by configurations.creating

dependencies {
    // --- WPILib Dependencies ---
    api("edu.wpi.first.wpilibNewCommands:wpilibNewCommands-java:2024.3.2")
    api("edu.wpi.first.wpimath:wpimath-java:2024.3.2")
    api("edu.wpi.first.wpiutil:wpiutil-java:2024.3.2")

    // --- Pi4J Hardware Dependencies ---
    api("com.pi4j:pi4j-core:2.6.0")
    api("com.pi4j:pi4j-plugin-raspberrypi:2.6.0")
    api("com.pi4j:pi4j-plugin-pigpio:2.6.0")

    // --- Internal & Version Catalog Dependencies ---
    api(libs.commons.math3)
    implementation(libs.guava)

    // --- Testing Framework ---
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.pi4j:pi4j-plugin-mock:2.6.0")
    testImplementation("org.slf4j:slf4j-simple:2.0.9")

    // --- WPILib Java Test Binaries ---
    testImplementation("edu.wpi.first.hal:hal-java:2024.3.2")
    testImplementation("edu.wpi.first.wpilibj:wpilibj-java:2024.3.2")
    testImplementation("edu.wpi.first.ntcore:ntcore-java:2024.3.2")
    testImplementation("edu.wpi.first.cscore:cscore-java:2024.3.2")
    testImplementation("edu.wpi.first.wpinet:wpinet-java:2024.3.2")

    // --- WPILib Native Desktop Binaries (Extracted for Test Runtime) ---
    val nativeDeps = listOf(
        "edu.wpi.first.hal:hal-cpp:2024.3.2:linuxx86-64@zip",
        "edu.wpi.first.wpimath:wpimath-cpp:2024.3.2:linuxx86-64@zip",
        "edu.wpi.first.wpiutil:wpiutil-cpp:2024.3.2:linuxx86-64@zip",
        "edu.wpi.first.wpinet:wpinet-cpp:2024.3.2:linuxx86-64@zip",
        "edu.wpi.first.ntcore:ntcore-cpp:2024.3.2:linuxx86-64@zip",
        "edu.wpi.first.cscore:cscore-cpp:2024.3.2:linuxx86-64@zip",
        "edu.wpi.first.thirdparty.frc2024.opencv:opencv-cpp:4.8.0-2:linuxx86-64@zip"
    )

    nativeDeps.forEach { dep ->
        nativeNatives(dep)
        testRuntimeOnly(dep)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Task to unpack .so files from JNI zip artifacts into build/jni/linuxx86-64
val extractNativeLibs = tasks.register<Copy>("extractNativeLibs") {
    from(nativeNatives.map { zip -> zipTree(zip) })
    into(layout.buildDirectory.dir("jni/linuxx86-64"))
    include("**/*.so", "**/*.dylib", "**/*.dll")
    eachFile {
        // Flatten directory structure so all .so files sit directly in build/jni/linuxx86-64/
        path = name 
    }
    includeEmptyDirs = false
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    
    // Ensure native shared libraries are extracted before tests run
    dependsOn(extractNativeLibs)

    // Tell the JVM worker where to find extracted .so shared libraries
    val jniDir = layout.buildDirectory.dir("jni/linuxx86-64").get().asFile.absolutePath
    systemProperty("java.library.path", jniDir)

    // Force AWT headless for WPILib utilities
    systemProperty("java.awt.headless", "true")

    // Print standard out/err directly to stdout so crash causes aren't swallowed
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}