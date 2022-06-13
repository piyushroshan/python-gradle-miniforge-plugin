package io.github.piyushroshan.python

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import java.io.File
import java.net.URL

open class MiniforgeSetupTask : DefaultTask() {

    init {
        group = "python"
        description = "Setup $DEFAULT_MINIFORGE_RELEASE"
        this.onlyIf {
            !project.condaBinDir.exists()
        }
    }

    @TaskAction
    fun setup(): ExecResult = with(project) {
        miniforgeDir.mkdirs()
        val miniforgeInstaller = miniforgeDir.resolve("$DEFAULT_MINIFORGE_RELEASE-$os-$arch.$exec")
        downloadMiniforge(miniforgeInstaller, miniforgeVersion)
        allowInstallerExecution(miniforgeInstaller)
        logger.lifecycle("Installing $DEFAULT_MINIFORGE_RELEASE...")
        exec {
            it.executable = miniforgeInstaller.absolutePath
            val execArgs = if (isWindows)
                listOf(
                    "/InstallationType=JustMe",
                    "/RegisterPython=0",
                    "/AddToPath=0",
                    "/S",
                    "/D=${miniforgeDir.absolutePath}"
                )
            else
                listOf("-b", "-u", "-p", miniforgeDir.absolutePath)
            it.args(execArgs)
        }
    }

    private fun Project.allowInstallerExecution(miniforgeInstaller: File) {
        if (!isWindows) {
            logger.lifecycle("Allowing user to run installer...")
            exec {
                it.executable = "chmod"
                it.args("u+x", miniforgeInstaller.absolutePath)
            }
        }
    }

    private fun downloadMiniforge(miniforgeFile: File, miniforgeVersion: String) {
        logger.lifecycle("Downloading $DEFAULT_MINIFORGE_RELEASE to: ${miniforgeFile.absolutePath}")
        val miniforgeInputStream = URL("https://github.com/conda-forge/miniforge/releases/download/${miniforgeVersion}/${miniforgeFile.name}").openStream()
        miniforgeInputStream.use { inputStream ->
            miniforgeFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
