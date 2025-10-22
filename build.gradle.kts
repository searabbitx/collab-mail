import org.gradle.jvm.tasks.Jar

plugins {
    id("java")
}

group = "io.searabbitx"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.portswigger.burp.extensions:montoya-api:2025.8")
//    implementation("org.apache.commons:commons-email:1.5")
    implementation("org.apache.commons:commons-email2-jakarta:2.0.0-M1")
    implementation("com.sun.mail:jakarta.mail:2.0.2")
    implementation("org.eclipse.angus:angus-mail:2.0.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    if ("1" == System.getenv("DEBUG")) {
        logger.log(LogLevel.WARN, "Compiling with -g option")
        options.compilerArgs.add("-g")
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
}

tasks.register<Jar>("jarDebug") {
    if ("1" == System.getenv("DEBUG")) {
        group = "build"
        description = "Builds a JAR with debug symbols enabled."
        logger.log(LogLevel.WARN, "Debug version")
        project.version = "1.0-debug"

        // Ensure outputs from compilation are included
        from(sourceSets.main.get().output)

        archiveClassifier.set("debug")

        dependsOn(tasks.named("compileJava"))
    }
}
