package com.github.alexxxdev.hrm.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

val jsonConfig = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

abstract class Config<T : Any>(name: String) {

    private var isEmpty = true
    private var isCorrect = false
    private val configFile = File(name)
    private val absolutePath: String = configFile.absolutePath

    protected abstract var model: T
    protected abstract fun prefillConfig(): T
    protected abstract fun serializer(): KSerializer<T>

    fun readConfig(): Boolean {
        return if (exists()) {
            read()
            if (isEmpty) {
                prefillConfigSave()
                Log.d("Need to fill out the config $absolutePath")
                false
            } else if (!isCorrect) {
                Log.d("Need to fill out the config $absolutePath")
                false
            } else {
                true
            }
        } else {
            if (createNewFile()) {
                prefillConfigSave()
                Log.d("Need to fill out the config $absolutePath")
            } else {
                Log.d("It is necessary to create and fill in the config $absolutePath")
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
            Log.d(model)
            isEmpty = false
            isCorrect = check()
        } catch (e: Exception) {
            Log.d(e)
            isEmpty = true
        }
    }

    private fun exists(): Boolean = configFile.exists()

    private fun createNewFile(): Boolean = configFile.createNewFile()

    @Suppress("FunctionOnlyReturningConstant")
    private fun check(): Boolean {
        return true
    }
}
