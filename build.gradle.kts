plugins {
    java
    `maven-publish`

    id("io.freefair.lombok") version "6.4.1"

    id("org.springframework.boot") version "2.6.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "pink.zak.discord.utils"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.9")
    implementation("com.google.code.gson:gson:2.9.0")

    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "pink.zak.discord"
            artifactId = "spring-boot-starter-discord"
            version = "1.0"

            from(components["java"])
        }
    }
}