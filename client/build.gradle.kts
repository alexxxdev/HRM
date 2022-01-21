plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    id("org.openjfx.javafxplugin")
}

group = project.property("HRMgroup") as String + ".client"
version = project.property("HRMversion") as String
val mainClazz = "$group.app.MyApp"
val applicationName = "HRM Client"

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("eu.hansolo:tilesfx:11.43")
    implementation("eu.hansolo:Medusa:11.5")
    implementation("eu.hansolo:regulators:11.7")
    implementation("org.kordamp.ikonli:ikonli-javafx:11.5.0")
    implementation("org.kordamp.ikonli:ikonli-material-pack:11.5.0")
    implementation("org.kordamp.ikonli:ikonli-materialdesign-pack:11.5.0")
    implementation("org.kordamp.ikonli:ikonli-weathericons-pack:11.5.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome-pack:11.5.0")
    implementation("io.ktor:ktor-network:1.6.7")
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-java:1.6.7")
    implementation("io.ktor:ktor-client-serialization:1.6.7")
    implementation("io.ktor:ktor-client-logging-jvm:1.6.7")
    /*implementation("com.dorkbox:SystemTray:3.17")
    implementation("net.java.dev.jna:jna:5.5.0")
    implementation("net.java.dev.jna:jna-platform:5.5.0")*/
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    implementation("org.slf4j:slf4j-simple:1.6.1")
    implementation("de.codecentric.centerdevice:javafxsvg:1.3.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

javafx {
    version = "14.0.2.1"
    modules = listOf("javafx.controls", "javafx.graphics")
    // configuration = "compileOnly"
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
