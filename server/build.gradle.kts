plugins {
    kotlin("jvm")
    application
}

group = "com.github.alexxxdev.hrm.server"
version = "1.0-SNAPSHOT"
val mainClazz = "$group.MainKt"
val applicationName = "HRM Server"

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-network:1.3.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

application {
    applicationName = rootProject.name
    mainClassName = mainClazz
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = version
        attributes["Main-Class"] = mainClazz
        attributes["Application-Name"] = applicationName
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
