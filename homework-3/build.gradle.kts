plugins {
    java
    war
    id("ua.edu.ukma.data-pipeline")
}

group = "ua.edu.ukma"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.named("compileJava") {
    dependsOn("validateCsv")
}

tasks.named("war") {
    dependsOn("generateManifest")
}

tasks.register<Copy>("exportResult") {
    dependsOn("war")

    from(layout.buildDirectory.dir("libs")) {
        include("*.war")
    }

    from(layout.buildDirectory.file("manifest.txt"))

    into(layout.projectDirectory.dir("release"))

    doLast {
        println("🚀 Реліз успішно скопійовано у папку 'release'!")
    }
}