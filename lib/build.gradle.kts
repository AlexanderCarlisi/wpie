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

dependencies {
    // --- WPILib Dependencies ---
    // Command-Based Framework (Command, SubsystemBase, CommandScheduler)
    api("edu.wpi.first.wpilibNewCommands:wpilibNewCommands-java:2024.3.2")
    
    // Math Utilities (SlewRateLimiter, PIDController, Trajectories)
    api("edu.wpi.first.wpimath:wpimath-java:2024.3.2")
    api("edu.wpi.first.wpiutil:wpiutil-java:2024.3.2")

    // --- Pi4J Hardware Dependencies ---
    api("com.pi4j:pi4j-core:2.6.0")
    api("com.pi4j:pi4j-plugin-raspberrypi:2.6.0")
    api("com.pi4j:pi4j-plugin-pigpio:2.6.0")

    // --- Internal & Version Catalog Dependencies ---
    api(libs.commons.math3)
    implementation(libs.guava)

    // --- Testing ---
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.pi4j:pi4j-plugin-mock:2.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.slf4j:slf4j-simple:2.0.9")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}