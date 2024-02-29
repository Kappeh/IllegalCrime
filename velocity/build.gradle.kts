plugins {
    id("java")
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
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    implementation(project(":core"))
}

val targetJavaVersion = 17

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
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
