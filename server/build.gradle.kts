plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "com.github.alexxxdev.hrm.server"
version = "1.0-SNAPSHOT"
val mainClazz = "$group.MainKt"
val applicationName = "HRM Server"

val os = System.getProperty("os.name")

dependencies {
    implementation(project(":core"))
    if(os.startsWith("Linux")) {
        implementation(project(":linux"))
    } else if(os.startsWith("Windows")){
        implementation(project(":windows"))
    }
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-network:1.3.2")
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
