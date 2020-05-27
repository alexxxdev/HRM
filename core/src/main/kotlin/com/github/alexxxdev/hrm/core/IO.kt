package com.github.alexxxdev.hrm.core

import java.io.InputStreamReader

class IO {
    @Throws(java.lang.Exception::class)
    fun getShellOutput(cmd: String): String {
        return getShellOutput(arrayOf(cmd))
    }

    fun getShellOutput(cmd: Array<String>): String {
        val p = Runtime.getRuntime().exec(cmd)
        val isr = InputStreamReader(p.inputStream)
        val sb = StringBuilder()
        while (true) {
            val x: Int = isr.read()
            if (x != -1) {
                sb.append(x.toChar())
            } else break
        }
        isr.close()
        p.destroy()
        return sb.toString()
    }
}
