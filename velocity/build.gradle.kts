import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.kappeh"
version = "1.0.0-SNAPSHOT"

base.archivesName = "illegalcrimevelocity"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "minebench-repo"
        url = uri("https://repo.minebench.de/")
    }
}

dependencies {
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    implementation(project(":core"))
    implementation("de.themoep:minedown-adventure:1.7.2-SNAPSHOT")
}

val targetJavaVersion = 17

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.named<ShadowJar>("shadowJar") {
    relocate("de.themoep.minedown", "org.kappeh.myvelocityplugin.libraries.minedown")
}

tasks.build {
    dependsOn("shadowJar")
}

val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")

val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val props = mapOf(
        "name" to rootProject.name,
        "version" to project.version,
    )
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

tasks.named("compileJava") {
    dependsOn("generateTemplates")
}

sourceSets.main.configure {
    java.srcDir(generateTemplates.map { it.outputs })
}
