import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("java")
    application
    id("com.github.node-gradle.node") version "7.1.0"
}

group = "com.discordsrv"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.4.0")
    implementation("org.slf4j:slf4j-jdk14:2.0.16")
    implementation("com.github.kevinsawicki:http-request:6.0")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("net.jodah:expiringmap:0.5.11")
}

application {
    mainClass.set("com.discordsrv.heads.Heads")
}

node {
    // Gradle downloads its own Node, so building doesn't need one installed
    download.set(true)
    version.set("24.21.0")
    npmInstallCommand.set("ci")
    nodeProjectDir.set(file("frontend"))
}

tasks {
    val buildFrontend = register<NpmTask>("buildFrontend") {
        description = "Builds the Next.js frontend into frontend/out."
        dependsOn(npmInstall)
        npmCommand.set(listOf("run", "build"))
        inputs.dir("frontend/app")
        inputs.dir("frontend/components")
        inputs.dir("frontend/lib")
        inputs.files("frontend/package.json", "frontend/package-lock.json", "frontend/next.config.ts", "frontend/tsconfig.json")
        outputs.dir("frontend/out")
    }
    processResources {
        // Served by Javalin from the classpath's static directory
        from(buildFrontend) { into("static") }
    }
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "processResources"))
        archiveFileName.set("DiscordSRV-Heads.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } + sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}
