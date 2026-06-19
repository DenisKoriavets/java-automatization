plugins {
    id("java")
    id("info.solidsoft.pitest") version "1.19.0"
}

group = "ua.edu.ukma"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")

    testImplementation("org.assertj:assertj-core:3.27.7")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

pitest {
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(setOf("ua.edu.ukma.delivery.*"))
    mutators.set(setOf("DEFAULTS"))
    outputFormats.set(setOf("HTML"))
}