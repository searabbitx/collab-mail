plugins {
    id("java")
}

group = "io.searabbitx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("net.portswigger.burp.extensions:montoya-api:2025.8")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
}