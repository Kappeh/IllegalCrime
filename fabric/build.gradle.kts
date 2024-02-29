plugins {
    id("fabric-loom") version "1.5-SNAPSHOT"
}

group = "org.kappeh"
version = "1.0.0-SNAPSHOT"

base.archivesName.set(project.property("archives_base_name") as String)

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

fabricApi {
    configureDataGeneration()
}

dependencies {
    // To change the versions see the gradle.properties file
    "minecraft"("com.mojang:minecraft:${project.property("minecraft_version")}")
    "mappings"("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    "modImplementation"("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    // Uncomment the following line to enable the deprecated Fabric API modules.
    // These are included in the Fabric API production distribution and allow you to update your mod to the latest modules at a later more convenient time.

    // "modImplementation"("net.fabricmc.fabric-api:fabric-api-deprecated:${project.property("fabric_version")}")

    implementation(project(":core"))
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

val targetJavaVersion = 17

tasks.withType<JavaCompile> {
    options.release.set(targetJavaVersion)
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

//jar {
//    from("LICENSE") {
//        rename { "${it}_${project.base.archivesName.get()}"}
//    }
//}

//// configure the maven publication
//publishing {
//    publications {
//        mavenJava(MavenPublication) {
//            from components.java
//        }
//    }
//
//    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
//    repositories {
//        // Add repositories to publish to here.
//        // Notice: This block does NOT have the same function as the block in the top level.
//        // The repositories here will be used for publishing your artifact, not for
//        // retrieving dependencies.
//    }
//}
