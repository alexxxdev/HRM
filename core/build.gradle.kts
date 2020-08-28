plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = project.property("HRMgroup") as String + ".core"
version = project.property("HRMversion") as String

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
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
