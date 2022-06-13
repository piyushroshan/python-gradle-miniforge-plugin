import org.jetbrains.changelog.date
import net.researchgate.release.GitAdapter.GitConfig
import net.researchgate.release.ReleaseExtension

plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.kotlinx.kover") version "0.5.1"
    id("com.gradle.plugin-publish") version "0.21.0"
    id("net.researchgate.release") version "2.8.1"
    id("org.jetbrains.changelog") version "1.3.1"
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.assertj:assertj-core:3.22.0")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks {
    test {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
    }
    "afterReleaseBuild" {
        dependsOn(
            "publish",
            "publishPlugins",
            "patchChangelog"
        )
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
fun ReleaseExtension.git(configure: GitConfig.() -> Unit) = (getProperty("git") as GitConfig).configure()

release {
    git {
        requireBranch = "main|\\d+\\.\\d+"
    }
}

gradlePlugin {
    plugins {
        create("python-gradle-miniforge-plugin") {
            id = "io.github.piyushroshan.python-gradle-miniforge-plugin"
            implementationClass = "io.github.piyushroshan.python.PythonPlugin"
            displayName =
                "Gradle plugin to run Python projects in Miniforge virtual env. https://github.com/piyushroshan/python-gradle-miniforge-plugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/piyushroshan/python-gradle-miniforge-plugin"
    vcsUrl = "https://github.com/piyushroshan/python-gradle-miniforge-plugin"
    description = "Gradle plugin to run Python projects."
    tags = listOf("python", "venv", "numpy", "miniforge", "conda", "scipy", "pandas")
}



// Configuring changelog Gradle plugin https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    header.set(provider { "[${version.get()}] - ${date()}" })
}
