plugins {
    id("java")
    id("checkstyle")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/DenisKoriavets/java-automatization")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
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
}

checkstyle {
    toolVersion = "10.12.4"
}

tasks.register<Test>("smokeTests") {
    useJUnitPlatform {
        includeTags("smoke")
    }
}

tasks.register<Test>("advancedTests") {
    useJUnitPlatform {
        includeTags("advanced")
    }
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}