buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.openjfx:javafx-plugin:0.0.8")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.28.0")
        classpath("no.tornado:fxlauncher-gradle-plugin:1.0.21.1")
    }
}
allprojects {
    apply {
        plugin("com.github.ben-manes.versions")
    }
    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
