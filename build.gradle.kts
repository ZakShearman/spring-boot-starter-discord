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
    publications {
        create<MavenPublication>("maven") { // Only used for local publishing so same as JitPack
            groupId = "com.github.ZakShearman"
            artifactId = "spring-boot-starter-discord"
            version = "local"

            from(components["java"])
        }
    }
}