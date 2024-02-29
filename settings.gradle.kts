rootProject.name = "IllegalCrime"

pluginManagement {
    repositories {
        mavenCentral()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
    }
}

include("core")
include("velocity")
include("paper")
include("fabric")
