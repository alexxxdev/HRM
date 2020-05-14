package com.github.alexxxdev.hrm.core

import java.net.JarURLConnection
import java.net.URL
import java.util.jar.Attributes
import java.util.jar.Manifest

fun HRMModel.getVersion(): String {
    val className: String = this::class.java.simpleName + ".class"
    val classPath: String = this::class.java.getResource(className).toString()
    val url = URL(classPath)
    val jarConnection: JarURLConnection = url.openConnection() as JarURLConnection
    val manifest: Manifest = jarConnection.manifest
    val attributes: Attributes = manifest.mainAttributes
    return attributes.getValue("Manifest-Version")
}
