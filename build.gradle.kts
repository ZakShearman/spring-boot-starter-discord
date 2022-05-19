plugins {
    java
    `maven-publish`

    id("io.freefair.lombok") version "6.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "pink.zak.discord.utils"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.12")
    implementation("com.google.code.gson:gson:2.9.0")

    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa:2.6.7")
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