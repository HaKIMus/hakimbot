import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val exposedVersion: String by project

plugins {
    kotlin("jvm") version "1.7.21"
    application
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.hakim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://raw.github.com/AcaiSoftware/chatgpt-java/repository")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("net.dv8tion:JDA:5.0.0-alpha.22")
    implementation("org.twitter4j:twitter4j-core:4.0.7")
    implementation("io.github.takke:jp.takke.twitter4j-v2:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("org.kodein.di:kodein-di:7.14.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.1.2")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("io.reactivex.rxjava2:rxjava:2.2.8")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "18"
}

application {
    mainClass.set("com.hakim.hakimbot.Main")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveFileName.set("${project.name}-${project.version}.jar")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.hakim.hakimbot.Main"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}