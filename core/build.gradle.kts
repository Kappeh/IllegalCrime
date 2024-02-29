plugins {
    id("java-library")
}

group = "org.kappeh"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
}
