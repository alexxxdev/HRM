package com.github.alexxxdev.hrm.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

val jsonConfig = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

abstract class Config<T>(name: String) {

    protected val configFile = File(name)
    protected abstract var model: T
    protected abstract fun prefillConfig(): T
    protected abstract fun serializer(): KSerializer<T>

    private var isEmpty = true
    private var isCorrect = false
    private val absolutePath: String = configFile.absolutePath

    fun readConfig(): Boolean {
        return if (exists()) {
            read()
            if (isEmpty) {
                prefillConfigSave()
                println("Need to fill out the config $absolutePath")
                false
            } else if (!isCorrect) {
                println("Need to fill out the config $absolutePath")
                false
            } else {
                true
            }
        } else {
            if (createNewFile()) {
                prefillConfigSave()
                println("Need to fill out the config $absolutePath")
            } else {
                println("It is necessary to create and fill in the config $absolutePath")
            }
            false
        }
    }

    private fun prefillConfigSave() {
        val config = jsonConfig.encodeToString(serializer(), prefillConfig())
        configFile.writeText(config)
    }

    private fun read() {
        try {
            val raw = configFile.readText()
            model = jsonConfig.decodeFromString(serializer(), raw)
            println(model)
            isEmpty = false
            isCorrect = check()
        } catch (e: Exception) {
            println(e)
            isEmpty = true
        }
    }

    private fun exists(): Boolean = configFile.exists()

    private fun createNewFile(): Boolean = configFile.createNewFile()

    private fun check(): Boolean {
        // TODO
        return true
    }
}
