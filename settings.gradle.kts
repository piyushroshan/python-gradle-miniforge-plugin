rootProject.name = "python-gradle-miniforge-plugin"

pluginManagement {
    val pythonPluginVersionForExamples: String by settings

    plugins {
        id("io.github.piyushroshan.python-gradle-miniforge-plugin") version pythonPluginVersionForExamples
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
