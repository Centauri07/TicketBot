plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm") version "1.5.31"
    java
}

group = "me.centauri07.ticketbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://m2.dv8tion.net/releases") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    implementation("net.dv8tion:JDA:4.3.0_300")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation(files("C:\\Users\\Andrei\\Documents\\Programming\\Libraries and APIs\\DCM\\build\\libs\\DiscordCommandManager-v1.0-alpha.jar"))

    implementation("com.google.code.gson:gson:2.8.8")
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
        options.forkOptions.executable = "javac"
        options.encoding = "UTF-8"
    }

    compileJava { options.encoding = "UTF-8" }

    jar {
        baseName = "ProximaCentauriBot"

        manifest {
            attributes["Main-Class"] = "me.centauri07.ticketbot.Main"
        }
    }

    shadowJar {
        archiveFileName.set("${project.rootProject.name}-v${project.version}.jar")
        destinationDir = file("C:\\Users\\Andrei\\Documents\\Discord")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}