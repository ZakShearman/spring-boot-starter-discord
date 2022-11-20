plugins {
    java
    `maven-publish`

    id("io.freefair.lombok") version "6.5.0.3"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "pink.zak.discord.utils"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.16")
    implementation("com.google.code.gson:gson:2.9.0")

    compileOnly("org.springframework.boot:spring-boot-starter:2.7.1")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") { // Only used for local publishing so same as JitPack
            groupId = "com.github.ZakShearman"
            artifactId = "spring-boot-starter-discord"
            version = "1.0"

            from(components["java"])
        }
    }
}