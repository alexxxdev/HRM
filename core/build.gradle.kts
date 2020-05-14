plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.github.alexxxdev.hrm.core"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = version
    }
}
