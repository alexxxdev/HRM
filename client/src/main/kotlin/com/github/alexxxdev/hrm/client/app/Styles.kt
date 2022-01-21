package com.github.alexxxdev.hrm.client.app

import eu.hansolo.medusa.Clock
import eu.hansolo.medusa.Fonts
import eu.hansolo.tilesfx.Tile
import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.px

@Suppress("MagicNumber")
class Styles : Stylesheet() {

    init {
        root {
            backgroundColor += Tile.BACKGROUND
        }
        s(clock) {
            // backgroundColor += Tile.BLUE
        }
        label and heading {
            fontSize = 12.px
            fontWeight = FontWeight.BOLD
            // textFill = Tile.BLUE
            backgroundColor += Tile.BLUE
        }
        label and header {
            // backgroundColor += Tile.BLUE
            font = Fonts.robotoRegular(14.0)
            textFill = Clock.BRIGHT_COLOR
        }
        label and weather {
            fontFamily = "Roboto"
            fontSize = 20.px
            // fontWeight = FontWeight.NORMAL
            textFill = Tile.BLUE
        }
        label and weather2 {
            fontFamily = "Roboto"
            fontSize = 15.px
            // fontWeight = FontWeight.NORMAL
            textFill = Tile.BLUE
        }
        label and weather3 {
            fontFamily = "Roboto"
            fontSize = 12.px
            // fontWeight = FontWeight.NORMAL
            textFill = Tile.BLUE
        }
    }

    companion object {
        val heading by cssclass()
        val weather by cssclass()
        val weather2 by cssclass()
        val weather3 by cssclass()
        val clock by cssclass()
        val header by cssclass()
    }
}
