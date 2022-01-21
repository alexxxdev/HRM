import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
        classpath(kotlin("serialization", version = "1.6.10"))
        classpath("org.openjfx:javafx-plugin:0.0.9")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.29.0")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
}

allprojects {
    apply(plugin = "com.github.ben-manes.versions")

    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }

    tasks.withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
}

val detektAll by tasks.registering(Detekt::class) {
    description = "Reformats whole code base."
    parallel = true
    disableDefaultRuleSets = false
    buildUponDefaultConfig = true
    autoCorrect = true
    setSource(files(projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    config.setFrom(files(projectDir.resolve("${rootProject.projectDir}/detekt/all.yml")))
    reports {
        xml.required.set(false)
        html.required.set(true)
        txt.required.set(true)
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

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
