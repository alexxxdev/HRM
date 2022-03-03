package com.github.alexxxdev.hrm.core

object Log {
    var debug: Boolean = false

    fun d(message: Any) {
        if (debug) {
            println(message)
        }
    }
}
