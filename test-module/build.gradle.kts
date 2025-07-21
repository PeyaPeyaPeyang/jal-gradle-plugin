plugins {
    id("java")
    id("tokyo.peya.langjal") version "0.0.1"
}

group = "tokyo.peya"
version = "0.0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.test {
    useJUnitPlatform()
}
