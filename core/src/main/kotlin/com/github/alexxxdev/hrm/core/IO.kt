package com.github.alexxxdev.hrm.core

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader

class IO {
    fun pln(txt: String) {
        println(txt)
    }

    @Throws(Exception::class)
    fun readFileRaw(fileLocation: String): String {
        val tbr: String
        val f = File(fileLocation)
        val bf = BufferedReader(FileReader(f))
        tbr = bf.readLine()
        bf.close()
        return tbr
    }

    fun writeToFile(content: String, fileName: String) {
        try {
            val f = File(fileName)
            if (!f.exists()) {
                if (!f.createNewFile()) {
                    throw java.lang.Exception("Insufficient Permissions to create $fileName")
                }
            }
            val bf = BufferedWriter(FileWriter(fileName))
            bf.write(content)
            bf.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(java.lang.Exception::class)
    fun readFileArranged(fileLocation: String, sep: String): Array<String> {
        return readFileRaw(fileLocation).split(sep).toTypedArray()
    }

    @Throws(java.lang.Exception::class)
    fun getShellOutput(cmd: String?): String {
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
