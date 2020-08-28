package com.github.alexxxdev.hrm.client

import eu.hansolo.medusa.Clock
import javafx.scene.control.Skin

class ClockX : Clock() {
    override fun createDefaultSkin(): Skin<*> {
        return SlimClockSkinX(this)
    }

    override fun getUserAgentStylesheet(): String {
        return javaClass.classLoader.getResource("clock.css")?.toExternalForm().orEmpty()
    }
}
