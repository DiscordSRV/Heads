plugins {
    id("java")
    application
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
    implementation("com.github.alexheretic:dynamics:4.0")
    implementation("net.jodah:expiringmap:0.5.11")
}

application {
    mainClass.set("com.discordsrv.heads.Heads")
}

tasks {
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
