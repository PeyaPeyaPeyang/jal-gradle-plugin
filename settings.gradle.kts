rootProject.name = "jal-gradle-plugin"
include("test-module")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
