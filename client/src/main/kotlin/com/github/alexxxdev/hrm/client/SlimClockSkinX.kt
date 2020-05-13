package com.github.alexxxdev.hrm.client

import eu.hansolo.medusa.Fonts
import eu.hansolo.medusa.skins.ClockSkinBase
import eu.hansolo.medusa.tools.Helper
import javafx.geometry.Insets
import javafx.scene.layout.*
import javafx.scene.shape.Arc
import javafx.scene.shape.Circle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeType
import javafx.scene.text.Text
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

class SlimClockSkinX(clock: ClockX) : ClockSkinBase(clock) {
    private val HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH")
    private val MINUTE_FORMATTER = DateTimeFormatter.ofPattern("mm")
    private val dateTextFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("cccc", clock.locale)
    private val dateNumberFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM YYYY", clock.locale)
    private var size = 0.0
    private var secondBackgroundCircle: Circle? = null
    private var dateText: Text? = null
    private var dateNumbers: Text? = null
    private var hour: Text? = null
    private var minute: Text? = null
    private var secondArc: Arc? = null
    private var pane: Pane? = null

    init {
        initGraphics()
        registerListeners()
    }

    override fun initGraphics() {

        if (clock.prefWidth.compareTo(0.0) <= 0 || clock.prefHeight.compareTo(0.0) <= 0 || clock.width.compareTo(0.0) <= 0 || clock.height.compareTo(
                0.0
            ) <= 0
        ) {
            if (clock.prefWidth > 0 && clock.prefHeight > 0) {
                clock.setPrefSize(clock.prefWidth, clock.prefHeight)
            } else {
                clock.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT)
            }
        }

        val time = clock.time

        secondBackgroundCircle = Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.48).apply {
            strokeWidth = PREFERRED_WIDTH * 0.008
            strokeType = StrokeType.CENTERED
            strokeLineCap = StrokeLineCap.ROUND
            fill = null
            stroke = Helper.getTranslucentColorFrom(clock.secondColor, 0.2)
            isVisible = clock.isSecondsVisible
            isManaged = clock.isSecondsVisible
        }

        dateText = Text(dateTextFormatter.format(time)).apply {
            isVisible = clock.isDayVisible
            isManaged = clock.isDayVisible
        }

        dateNumbers = Text(dateNumberFormatter.format(time)).apply {
            isVisible = clock.isDateVisible
            isManaged = clock.isDateVisible
        }

        hour = Text(HOUR_FORMATTER.format(time))
        hour?.fill = clock.hourColor

        minute = Text(MINUTE_FORMATTER.format(time))
        minute?.fill = clock.minuteColor

        secondArc = Arc(
            PREFERRED_WIDTH * 0.5,
            PREFERRED_HEIGHT * 0.5,
            PREFERRED_WIDTH * 0.96,
            PREFERRED_WIDTH * 0.48,
            90.0,
            (-6 * clock.time.second).toDouble()
        ).apply {
            strokeWidth = PREFERRED_WIDTH * 0.008
            strokeType = StrokeType.CENTERED
            strokeLineCap = StrokeLineCap.ROUND
            fill = null
            stroke = clock.secondColor
            isVisible = clock.isSecondsVisible
            isManaged = clock.isSecondsVisible
        }

        pane = Pane(secondBackgroundCircle, dateText, dateNumbers, hour, minute, secondArc).apply {
            border = Border(
                BorderStroke(
                    clock.borderPaint,
                    BorderStrokeStyle.SOLID,
                    CornerRadii(1024.0),
                    BorderWidths(clock.borderWidth)
                )
            )
            background = Background(BackgroundFill(clock.backgroundPaint, CornerRadii(1024.0), Insets.EMPTY))
        }

        children.setAll(pane)
    }

    override fun updateAlarms() {

    }

    override fun resize() {
        val width = clock.width - clock.insets.left - clock.insets.right
        val height = clock.height - clock.insets.top - clock.insets.bottom
        size = if (width < height) width else height

        if (size > 0) {
            val center = size * 0.5
            pane?.setMaxSize(size, size)
            pane?.relocate((clock.width - size) * 0.5, (clock.height - size) * 0.5)
            secondBackgroundCircle?.centerX = center
            secondBackgroundCircle?.centerY = center
            secondBackgroundCircle?.radius = size * 0.48590226
            secondBackgroundCircle?.strokeWidth = size * 0.02819549
            secondArc?.centerX = center
            secondArc?.centerY = center
            secondArc?.radiusX = size * 0.48590226
            secondArc?.radiusY = size * 0.48590226
            secondArc?.strokeWidth = size * 0.02819549
            dateText?.font = Fonts.robotoLight(size * 0.08082707)
            dateNumbers?.font = Fonts.robotoLight(size * 0.08082707)
            hour?.font = Fonts.robotoMedium(size * 0.328)
            minute?.font = Fonts.robotoThin(size * 0.328)
        }
    }

    override fun updateTime(TIME: ZonedDateTime) {
        if (dateText?.isVisible == true) {
            dateText?.text = dateTextFormatter.format(TIME)
            Helper.adjustTextSize(dateText, 0.6 * size, size * 0.08)
            dateText?.relocate((size - dateText?.layoutBounds?.width!!) * 0.5, size * 0.22180451)
        }
        if (dateNumbers?.isVisible == true) {
            dateNumbers?.text = dateNumberFormatter.format(TIME)
            Helper.adjustTextSize(dateNumbers, 0.6 * size, size * 0.08)
            dateNumbers?.relocate((size - dateNumbers?.layoutBounds?.width!!) * 0.5, size * 0.68984962)
        }

        hour?.text = HOUR_FORMATTER.format(TIME)
        Helper.adjustTextSize(hour, 0.4 * size, 0.328 * size)
        hour?.relocate(0.136 * size, (size - hour?.layoutBounds?.height!!) * 0.5)

        minute?.text = MINUTE_FORMATTER.format(TIME)
        Helper.adjustTextSize(minute, 0.4 * size, 0.328 * size)
        minute?.relocate(0.544 * size, (size - minute?.layoutBounds?.height!!) * 0.5)

        if (secondBackgroundCircle?.isVisible == true) {
            secondArc?.length = (-6 * TIME.second).toDouble()
            if (clock.isDiscreteSeconds) {
                secondArc?.length = (-6 * TIME.second).toDouble()
            } else {
                secondArc?.length = -6 * TIME.second - TIME[ChronoField.MILLI_OF_SECOND] * 0.006
            }
        }
    }

    override fun redraw() {
        pane?.border = Border(
            BorderStroke(
                clock.borderPaint,
                BorderStrokeStyle.SOLID,
                CornerRadii(1024.0),
                BorderWidths(clock.borderWidth / 250 * size)
            )
        )
        pane?.background = Background(BackgroundFill(clock.backgroundPaint, CornerRadii(1024.0), Insets.EMPTY))

        secondBackgroundCircle?.stroke = Helper.getTranslucentColorFrom(clock.secondColor, 0.2)
        secondArc?.stroke = clock.secondColor

        hour?.fill = clock.hourColor
        minute?.fill = clock.minuteColor

        dateText?.fill = clock.dateColor
        dateNumbers?.fill = clock.dateColor

        hour?.fill = clock.hourColor
        minute?.fill = clock.minuteColor

        val time = clock.time
        updateTime(time)
    }

    override fun handleEvents(EVENT_TYPE: String?) {
        super.handleEvents(EVENT_TYPE)
        if ("VISIBILITY" == EVENT_TYPE) {
            val isDateVisible = clock.isDateVisible
            val isDayVisible = clock.isDayVisible
            dateText?.isVisible = isDayVisible
            dateText?.isManaged = isDayVisible
            dateNumbers?.isVisible = isDateVisible
            dateNumbers?.isManaged = isDateVisible
            val isSecondsVisible = clock.isSecondsVisible
            secondBackgroundCircle?.isVisible = isSecondsVisible
            secondBackgroundCircle?.isManaged = isSecondsVisible
            secondArc?.isVisible = isSecondsVisible
            secondArc?.isManaged = isSecondsVisible
        } else if ("FINISHED" == EVENT_TYPE) {
        }
    }
}