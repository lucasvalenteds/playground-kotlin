import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.0"
    id("com.adarshr.test-logger") version "2.1.0"
    id("com.bmuschko.docker-remote-api") version "6.6.1"
    id("com.github.ben-manes.versions") version "0.29.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.14"
}

group = "com.playground"
version = "0.1.0"

repositories {
    jcenter()
    maven(url = "https://jitpack.io")
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("com.adarshr.test-logger")
    }

    repositories {
        jcenter()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    dependencies {
        implementation("org.slf4j", "slf4j-nop", "2.0.0-alpha1") // Disables 3rd-party logs

        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))

        testImplementation("org.assertj", "assertj-core", "3.17.2")
        testImplementation("io.mockk", "mockk", "1.10.0")
        testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.0-M1")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            suppressWarnings = true
        }
    }

    tasks.withType<Test> {
        failFast = true
        useJUnitPlatform()
    }

    sourceSets {
        create("integTest") {
            java.srcDir(file("src/integTest/kotlin"))
            resources.srcDir(file("src/integTest/resources"))
            compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
            runtimeClasspath += output + compileClasspath
        }
    }

    tasks.register<Test>("integTest") {
        description = "Runs the integration tests."
        group = "verification"
        testClassesDirs = sourceSets["integTest"].output.classesDirs
        classpath = sourceSets["integTest"].runtimeClasspath
    }

    sourceSets {
        create("e2eTest") {
            java.srcDir(file("src/e2eTest/kotlin"))
            resources.srcDir(file("src/e2eTest/resources"))
            compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
            runtimeClasspath += output + compileClasspath
        }
    }

    tasks.register<Test>("e2eTest") {
        description = "Runs End-to-End tests."
        group = "verification"
        testClassesDirs = sourceSets["e2eTest"].output.classesDirs
        classpath = sourceSets["e2eTest"].runtimeClasspath
    }
}
