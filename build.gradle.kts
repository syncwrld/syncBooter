import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "me.syncwrld"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://libraries.minecraft.net/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("com.esotericsoftware.yamlbeans:yamlbeans:1.17")
    implementation("team.unnamed:inject:2.0.1")
    implementation("net.dv8tion:JDA:5.0.0-beta.13") {
        exclude(group = "club.minnced", module = "opus-java")
    }
    implementation("org.ow2.asm:asm:9.1")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.kyori:adventure-text-serializer-gson:4.16.0")
    implementation("com.github.syncwrld:reflections:v0.10.2.1")
    implementation("commons-cli:commons-cli:1.6.0")

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.lucko:helper:5.6.13")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("com.mojang:authlib:1.5.21")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.register<DefaultTask>("copyFinalJarToTarget") {
    dependsOn(tasks.named("shadowJar"))
    doLast {
        val targetJarDirectory: Path = project.projectDir.toPath().resolve("target")
        Files.createDirectories(targetJarDirectory)

        val shadowJar = tasks.named<ShadowJar>("shadowJar").get()
        val shadowJarFile = shadowJar.archiveFile.get().asFile.toPath().toAbsolutePath()
        val targetJarPath = targetJarDirectory.resolve("${shadowJar.archiveBaseName.get()}.jar")

        Files.copy(shadowJarFile, targetJarPath, StandardCopyOption.REPLACE_EXISTING)
    }
}

tasks {
    build {
        dependsOn("copyFinalJarToTarget")
    }
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("syncBooter")
    archiveClassifier.set("")

    relocate("com.google.gson", "${project.group}.booter.libs.google.gson")
    relocate("com.google.common", "${project.group}.booter.libs.google.guava")
    relocate("com.google.errorprone", "${project.group}.booter.libs.google.errorprone")
    relocate("com.google.j2objc", "${project.group}.booter.libs.google.j2objc")
    relocate("com.esotericsoftware.yamlbeans", "${project.group}.booter.libs.google.yamlbeans")
    relocate("com.google.thirdparty", "${project.group}.booter.libs.google.thirdparty")
    relocate("com.zaxxer.hikari", "${project.group}.booter.libs.hikari")
    relocate("net.kyori", "${project.group}.booter.libs.google.kyori")
    relocate("org.apache.commons", "${project.group}.booter.libs.apccommons")
    relocate("org.intellij.lang.annotations", "${project.group}.booter.libs.ijann")
    relocate("org.jetbrains.annotations", "${project.group}.booter.libs.jtann")
    relocate("org.objectweb.asm", "${project.group}.booter.libs.asm")
    relocate("org.reflections", "${project.group}.booter.libs.reflections")
    relocate("org.checkerframework", "${project.group}.booter.libs.checkerfw")
    relocate("me.lucko.helper", "${project.group}.booter.libs.helper")
    relocate("me.lucko.shadow", "${project.group}.booter.libs.shadow")
    relocate("team.unnamed", "${project.group}.booter.libs.unnamed")
    relocate("javassist", "${project.group}.booter.libs.javassist")
    relocate("javax", "${project.group}.booter.libs.javax")
}
