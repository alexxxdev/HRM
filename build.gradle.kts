import io.gitlab.arturbosch.detekt.Detekt

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.openjfx:javafx-plugin:0.0.8")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.28.0")
        classpath("no.tornado:fxlauncher-gradle-plugin:1.0.21.1")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("1.9.0")
}

allprojects {
    apply(plugin = "com.github.ben-manes.versions")

    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.9.0")
}

val detektFormat by tasks.registering(Detekt::class) {
    description = "Reformats whole code base."
    parallel = true
    disableDefaultRuleSets = true
    buildUponDefaultConfig = true
    autoCorrect = true
    setSource(files(projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    config.setFrom(files(projectDir.resolve("${rootProject.projectDir}/detekt/format.yml")))
    reports {
        xml.enabled = false
        html.enabled = true
        txt.enabled = true
    }
}

tasks {
    withType<Detekt> {
        this.jvmTarget = "1.8"
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
