package com.github.alexxxdev.hrm.client.app

import eu.hansolo.tilesfx.Tile
import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val clock by cssclass()
    }

    init {
        root {
            backgroundColor += Tile.BACKGROUND
        }
        s(clock) {
            //backgroundColor += Tile.BLUE
        }
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
    }
}