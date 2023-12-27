import org.gradle.api.JavaVersion.VERSION_21

plugins {
    java
    `maven-publish`

    id("io.freefair.lombok") version "8.4"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "pink.zak.discord.utils"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.18")
    implementation("com.google.code.gson:gson:2.10.1")

    compileOnly("org.springframework.boot:spring-boot-starter:3.2.0")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.2.0")
}

java {
    sourceCompatibility = VERSION_21
    targetCompatibility = VERSION_21
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "development"
            url = uri("https://repo.emortal.dev/snapshots")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_SECRET")
            }
        }
        maven {
            name = "release"
            url = uri("https://repo.emortal.dev/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_SECRET")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "pink.zak.discord.utils"
            artifactId = "spring-boot-starter-discord"

            val commitHash = System.getenv("COMMIT_HASH_SHORT")
            val releaseVersion = System.getenv("RELEASE_VERSION")
            version = commitHash ?: releaseVersion ?: "local"

            from(components["java"])
        }
    }
}