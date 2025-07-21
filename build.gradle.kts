plugins {
    id("java")
    id("java-gradle-plugin")
    id("maven-publish")
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

    implementation("org.jetbrains:annotations:26.0.2")
    annotationProcessor("org.jetbrains:annotations:26.0.2")

    implementation("tokyo.peya:langjal:0.0.1")
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        create("jal") {
            id = "tokyo.peya.langjal"
            implementationClass = "tokyo.peya.langjal.gradle.JALPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "jal-gradle-plugin"

            from(components["java"])
        }
    }
}
