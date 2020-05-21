plugins {
    kotlin("jvm")
}

group = "com.github.alexxxdev.hrm.linux"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core"))
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
