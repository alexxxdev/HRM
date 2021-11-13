package com.github.alexxxdev.hrm.client

import eu.hansolo.medusa.Fonts
import eu.hansolo.medusa.Gauge
import eu.hansolo.medusa.skins.GaugeSkinBase
import eu.hansolo.medusa.tools.Helper
import eu.hansolo.medusa.tools.Statistics
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.CubicCurveTo
import javafx.scene.shape.Line
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.shape.PathElement
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.text.Text
import javafx.util.Pair
import java.util.Collections
import java.util.LinkedList
import java.util.Locale

/**
 * Created by hansolo on 05.12.16.
 */
class MyTileSparklineSkin(gauge: Gauge) : GaugeSkinBase(gauge) {
    // private var size = 0.0
    private var width = 0.0
    private var height = 0.0
    private var titleText: Text? = null
    private var valueText: Text? = null
    private var unitText: Text? = null
    private var averageText: Text? = null
    private var highText: Text? = null
    private var lowText: Text? = null
    private var subTitleText: Text? = null
    private var graphBounds: Rectangle? = null
    private var pathElements: MutableList<PathElement>? = null
    private var sparkLine: Path? = null
    private var dot: Circle? = null
    private var stdDeviationArea: Rectangle? = null
    private var averageLine: Line? = null
    private var pane: Pane? = null
    private var low: Double
    private var high: Double
    private var minValue: Double
    private var maxValue: Double
    private var range: Double
    private var stdDeviation: Double
    private var formatString: String
    private var locale: Locale
    private var noOfDatapoints: Int
    private val dataList: MutableList<Double?>
    private val currentValueListener: InvalidationListener
    private val averagingListener: InvalidationListener

    // ******************** Initialization ************************************
    private fun initGraphics() {
        // Set initial size
        if (java.lang.Double.compare(gauge.prefWidth, 0.0) <= 0 || java.lang.Double.compare(
                gauge.prefHeight,
                0.0
            ) <= 0 || java.lang.Double.compare(gauge.width, 0.0) <= 0 || java.lang.Double.compare(
                gauge.height,
                0.0
            ) <= 0
        ) {
            if (gauge.prefWidth > 0 && gauge.prefHeight > 0) {
                gauge.setPrefSize(gauge.prefWidth, gauge.prefHeight)
            } else {
                gauge.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT)
            }
        }
        graphBounds =
            Rectangle(PREFERRED_WIDTH * 0.05, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.9, PREFERRED_HEIGHT * 0.45)
        titleText = Text(gauge.title)
        titleText!!.fill = gauge.titleColor
        Helper.enableNode(titleText, !gauge.title.isEmpty())
        valueText = Text(String.format(locale, formatString, gauge.value))
        valueText!!.fill = gauge.valueColor
        Helper.enableNode(valueText, gauge.isValueVisible)
        unitText = Text(gauge.unit)
        unitText!!.fill = gauge.unitColor
        Helper.enableNode(unitText, !gauge.unit.isEmpty())
        averageText = Text(String.format(locale, formatString, gauge.average))
        averageText!!.fill = gauge.averageColor
        Helper.enableNode(averageText, gauge.isAverageVisible)
        highText = Text()
        highText!!.textOrigin = VPos.BOTTOM
        highText!!.fill = gauge.valueColor
        lowText = Text()
        lowText!!.textOrigin = VPos.TOP
        lowText!!.fill = gauge.valueColor
        subTitleText = Text(gauge.subTitle)
        subTitleText!!.textOrigin = VPos.TOP
        subTitleText!!.fill = gauge.subTitleColor
        stdDeviationArea = Rectangle()
        Helper.enableNode(stdDeviationArea, gauge.isAverageVisible)
        averageLine = Line()
        averageLine!!.stroke = gauge.averageColor
        averageLine!!.strokeDashArray.addAll(PREFERRED_WIDTH * 0.005, PREFERRED_WIDTH * 0.005)
        Helper.enableNode(averageLine, gauge.isAverageVisible)
        pathElements = ArrayList(noOfDatapoints)
        (pathElements as ArrayList<PathElement>).add(0, MoveTo())
        for (i in 1 until noOfDatapoints) {
            (pathElements as ArrayList<PathElement>).add(i, LineTo())
        }
        sparkLine = Path()
        sparkLine!!.elements.addAll(pathElements as ArrayList<PathElement>)
        sparkLine!!.fill = null
        sparkLine!!.stroke = gauge.barColor
        sparkLine!!.strokeWidth = PREFERRED_WIDTH * 0.0075
        sparkLine!!.strokeLineCap = StrokeLineCap.ROUND
        sparkLine!!.strokeLineJoin = StrokeLineJoin.ROUND
        dot = Circle()
        dot!!.fill = gauge.barColor
        pane = Pane(
            titleText,
            valueText,
            unitText,
            stdDeviationArea,
            averageLine,
            sparkLine,
            dot,
            averageText,
            highText,
            lowText,
            subTitleText
        )
        pane!!.border = Border(
            BorderStroke(
                gauge.borderPaint,
                BorderStrokeStyle.SOLID,
                CornerRadii(PREFERRED_WIDTH * 0.025),
                BorderWidths(gauge.borderWidth)
            )
        )
        pane!!.background = Background(
            BackgroundFill(
                gauge.backgroundPaint,
                CornerRadii(PREFERRED_WIDTH * 0.025),
                Insets.EMPTY
            )
        )
        children.setAll(pane)
    }

    override fun registerListeners() {
        super.registerListeners()
        gauge.currentValueProperty().addListener(currentValueListener)
        gauge.averagingPeriodProperty().addListener(averagingListener)
    }

    // ******************** Methods *******************************************
    @Suppress("ControlFlowWithEmptyBody")
    override fun handleEvents(EVENT_TYPE: String) {
        super.handleEvents(EVENT_TYPE)
        if ("RECALC" == EVENT_TYPE) {
            minValue = gauge.minValue
            maxValue = gauge.maxValue
            range = gauge.range
            redraw()
        } else if ("VISIBILITY" == EVENT_TYPE) {
            Helper.enableNode(titleText, !gauge.title.isEmpty())
            Helper.enableNode(valueText, gauge.isValueVisible)
            Helper.enableNode(unitText, !gauge.unit.isEmpty())
            Helper.enableNode(subTitleText, !gauge.subTitle.isEmpty())
            Helper.enableNode(averageLine, gauge.isAverageVisible)
            Helper.enableNode(averageText, gauge.isAverageVisible)
            Helper.enableNode(stdDeviationArea, gauge.isAverageVisible)
            redraw()
        } else if ("SECTION" == EVENT_TYPE) {
        } else if ("ALERT" == EVENT_TYPE) {
        } else if ("VALUE" == EVENT_TYPE) {
            if (gauge.isAnimated) {
                gauge.isAnimated = false
            }
            if (!gauge.isAveragingEnabled) {
                gauge.isAveragingEnabled = true
            }
            val value = Helper.clamp(minValue, maxValue, gauge.value)
            addData(value)
            drawChart(value)
        } else if ("CURRENT_VALUE" == EVENT_TYPE) {
        } else if ("AVERAGING_PERIOD" == EVENT_TYPE) {
            noOfDatapoints = gauge.averagingPeriod
            dataList.clear()
            // To get smooth lines in the chart we need at least 4 values
            require(noOfDatapoints >= 4) { "Please increase the averaging period to a value larger than 3." }
            for (i in 0 until noOfDatapoints) {
                dataList.add(minValue)
            }
            pathElements!!.clear()
            pathElements!!.add(0, MoveTo())
            for (i in 1 until noOfDatapoints) {
                pathElements!!.add(i, LineTo())
            }
            sparkLine!!.elements.setAll(pathElements)
            redraw()
        }
    }

    private fun addData(VALUE: Double) {
        if (dataList.size <= noOfDatapoints) {
            Collections.rotate(dataList, -1)
            dataList[noOfDatapoints - 1] = VALUE
        } else {
            dataList.add(VALUE)
        }
        stdDeviation = Statistics.getStdDev(dataList)
    }

    private fun drawChart(VALUE: Double) {
        low = Statistics.getMin(dataList)
        high = Statistics.getMax(dataList)
        if (java.lang.Double.compare(low, high) == 0) {
            low = minValue
            high = maxValue
        }
        range = high - low
        val minX = graphBounds!!.x
        val maxX = minX + graphBounds!!.width
        val minY = graphBounds!!.y
        val maxY = minY + graphBounds!!.height
        val stepX = graphBounds!!.width / (noOfDatapoints - 1)
        val stepY = graphBounds!!.height / range
        if (gauge.isSmoothing) {
            smooth(dataList)
        } else {
            val begin = pathElements!![0] as MoveTo
            begin.x = minX
            begin.y = maxY - Math.abs(low - dataList[0]!!) * stepY
            for (i in 1 until noOfDatapoints - 1) {
                val lineTo = pathElements!![i] as LineTo
                lineTo.x = minX + i * stepX
                lineTo.y = maxY - Math.abs(low - dataList[i]!!) * stepY
            }
            val end = pathElements!![noOfDatapoints - 1] as LineTo
            end.x = maxX
            end.y = maxY - Math.abs(low - dataList[noOfDatapoints - 1]!!) * stepY
            dot!!.centerX = maxX
            dot!!.centerY = end.y
        }
        val average = gauge.average
        val averageY = Helper.clamp(minY, maxY, maxY - Math.abs(low - average) * stepY)
        averageLine!!.startX = minX
        averageLine!!.endX = maxX
        averageLine!!.startY = averageY
        averageLine!!.endY = averageY
        stdDeviationArea!!.y = averageLine!!.startY - stdDeviation * 0.5 * stepY
        stdDeviationArea!!.height = stdDeviation * stepY
        valueText!!.text =
            Helper.formatNumber(gauge.locale, gauge.formatString, gauge.decimals, VALUE)
        averageText!!.text = String.format(locale, formatString, average)
        highText!!.text = String.format(locale, formatString, high)
        lowText!!.text = String.format(locale, formatString, low)
        resizeDynamicText()
    }

    override fun dispose() {
        gauge.currentValueProperty().removeListener(currentValueListener)
        gauge.averagingPeriodProperty().removeListener(averagingListener)
        super.dispose()
    }

    // ******************** Smoothing *****************************************
    fun smooth(DATA_LIST: List<Double?>) {
        val size = DATA_LIST.size
        val x = DoubleArray(size)
        val y = DoubleArray(size)
        low = Statistics.getMin(DATA_LIST)
        high = Statistics.getMax(DATA_LIST)
        if (java.lang.Double.compare(low, high) == 0) {
            low = minValue
            high = maxValue
        }
        range = high - low
        val minX = graphBounds!!.x
        val maxX = minX + graphBounds!!.width
        val minY = graphBounds!!.y
        val maxY = minY + graphBounds!!.height
        val stepX = graphBounds!!.width / (noOfDatapoints - 1)
        val stepY = graphBounds!!.height / range
        for (i in 0 until size) {
            x[i] = minX + i * stepX
            y[i] = maxY - Math.abs(low - DATA_LIST[i]!!) * stepY
        }
        val px = computeControlPoints(x)
        val py = computeControlPoints(y)
        sparkLine!!.elements.clear()
        for (i in 0 until size - 1) {
            sparkLine!!.elements.add(MoveTo(x[i], y[i]))
            sparkLine!!.elements.add(
                CubicCurveTo(
                    px.key[i]!!,
                    py.key[i]!!,
                    px.value[i]!!,
                    py.value[i]!!,
                    x[i + 1],
                    y[i + 1]
                )
            )
        }
        dot!!.centerX = maxX
        dot!!.centerY = y[size - 1]
    }

    private fun computeControlPoints(K: DoubleArray): Pair<Array<Double?>, Array<Double?>> {
        val n = K.size - 1
        val p1 = arrayOfNulls<Double>(n)
        val p2 = arrayOfNulls<Double>(n)

        /*rhs vector*/
        val a = DoubleArray(n)
        val b = DoubleArray(n)
        val c = DoubleArray(n)
        val r = DoubleArray(n)

        /*left most segment*/a[0] = 0.0
        b[0] = 2.0
        c[0] = 1.0
        r[0] = K[0] + 2 * K[1]

        /*internal segments*/for (i in 1 until n - 1) {
            a[i] = 1.0
            b[i] = 4.0
            c[i] = 1.0
            r[i] = 4 * K[i] + 2 * K[i + 1]
        }

        /*right segment*/a[n - 1] = 2.0
        b[n - 1] = 7.0
        c[n - 1] = 0.0
        r[n - 1] = 8 * K[n - 1] + K[n]

        /*solves Ax = b with the Thomas algorithm*/for (i in 1 until n) {
            val m = a[i] / b[i - 1]
            b[i] = b[i] - m * c[i - 1]
            r[i] = r[i] - m * r[i - 1]
        }
        p1[n - 1] = r[n - 1] / b[n - 1]
        for (i in n - 2 downTo 0) {
            p1[i] = (r[i] - c[i] * p1[i + 1]!!) / b[i]
        }
        for (i in 0 until n - 1) {
            p2[i] = 2 * K[i + 1] - p1[i + 1]!!
        }
        p2[n - 1] = 0.5 * (K[n] + p1[n - 1]!!)
        return Pair(p1, p2)
    }

    // ******************** Resizing ******************************************
    private fun resizeDynamicText() {
        var maxWidth = if (unitText!!.isManaged) width * 0.725 else width * 0.9
        var fontSize = width * 0.14
        valueText!!.font = Fonts.latoRegular(fontSize)
        if (valueText!!.layoutBounds.width > maxWidth) {
            Helper.adjustTextSize(valueText, maxWidth, fontSize)
        }
        if (unitText!!.isVisible) {
            valueText!!.relocate(
                width * 0.925 - valueText!!.layoutBounds.width - unitText!!.layoutBounds.width,
                height * 0.23
            )
        } else {
            valueText!!.relocate(width * 0.95 - valueText!!.layoutBounds.width, height * 0.15)
        }
        maxWidth = width * 0.3
        fontSize = width * 0.05
        averageText!!.font = Fonts.latoRegular(fontSize)
        if (averageText!!.layoutBounds.width > maxWidth) {
            Helper.adjustTextSize(averageText, maxWidth, fontSize)
        }
        if (averageLine!!.startY < graphBounds!!.y + graphBounds!!.height * 0.5) {
            averageText!!.y = averageLine!!.startY + height * 0.0425
        } else {
            averageText!!.y = averageLine!!.startY - height * 0.0075
        }
        highText!!.font = Fonts.latoRegular(fontSize)
        if (highText!!.layoutBounds.width > maxWidth) {
            Helper.adjustTextSize(highText, maxWidth, fontSize)
        }
        highText!!.y = graphBounds!!.y - height * 0.0125
        lowText!!.font = Fonts.latoRegular(fontSize)
        if (lowText!!.layoutBounds.width > maxWidth) {
            Helper.adjustTextSize(lowText, maxWidth, fontSize)
        }
        lowText!!.y = height * 0.9
    }

    private fun resizeStaticText() {
        var maxWidth = width * 0.9
        var fontSize = width * 0.06
        titleText!!.font = Fonts.latoRegular(fontSize)
        if (titleText!!.layoutBounds.width > maxWidth) {
            Helper.adjustTextSize(titleText, maxWidth, fontSize)
        }
        titleText!!.relocate(width * 0.05, height * 0.05)
        maxWidth = width * 0.15
        unitText!!.font = Fonts.latoRegular(fontSize)
        if (unitText!!.layoutBounds.width > maxWidth) {
            Helper.adjustTextSize(unitText, maxWidth, fontSize)
        }
        unitText!!.relocate(width * 0.95 - unitText!!.layoutBounds.width, height * 0.3375)
        averageText!!.x = width * 0.05
        highText!!.x = width * 0.05
        lowText!!.x = width * 0.05
        maxWidth = width * 0.75
        fontSize = width * 0.05
        subTitleText!!.font = Fonts.latoRegular(fontSize)
        if (subTitleText!!.layoutBounds.width > maxWidth) {
            Helper.adjustTextSize(subTitleText, maxWidth, fontSize)
        }
        subTitleText!!.relocate(width * 0.95 - subTitleText!!.layoutBounds.width, height * 0.9)
    }

    override fun resize() {
        width = gauge.width - gauge.insets.left - gauge.insets.right
        height = gauge.height - gauge.insets.top - gauge.insets.bottom
        // size = width//if (width < height) width else height
        if (width > 0 && height > 0) {
            pane!!.setMaxSize(width, height)
            pane!!.relocate((width) * 0.5, (height) * 0.5)
            graphBounds = Rectangle(width * 0.05, height * 0.5, width * 0.9, height * 0.39)
            stdDeviationArea!!.x = graphBounds!!.x
            stdDeviationArea!!.width = graphBounds!!.width
            averageLine!!.strokeDashArray.setAll(graphBounds!!.width * 0.01, graphBounds!!.width * 0.01)
            drawChart(gauge.value)
            sparkLine!!.strokeWidth = width * 0.01
            dot!!.radius = width * 0.014
            resizeStaticText()
            resizeDynamicText()
        }
    }

    override fun redraw() {
        pane!!.border = Border(
            BorderStroke(
                gauge.borderPaint,
                BorderStrokeStyle.SOLID,
                CornerRadii(width * 0.025),
                BorderWidths(gauge.borderWidth / PREFERRED_WIDTH * width)
            )
        )
        pane!!.background = Background(
            BackgroundFill(
                gauge.backgroundPaint,
                CornerRadii(width * 0.025),
                Insets.EMPTY
            )
        )
        locale = gauge.locale
        formatString = StringBuilder("%.").append(Integer.toString(gauge.decimals)).append("f").toString()
        titleText!!.text = gauge.title
        subTitleText!!.text = gauge.subTitle
        resizeStaticText()
        titleText!!.fill = gauge.titleColor
        valueText!!.fill = gauge.valueColor
        averageText!!.fill = gauge.averageColor
        highText!!.fill = gauge.valueColor
        lowText!!.fill = gauge.valueColor
        subTitleText!!.fill = gauge.subTitleColor
        sparkLine!!.stroke = gauge.barColor
        stdDeviationArea!!.fill = Helper.getTranslucentColorFrom(gauge.averageColor, 0.1)
        averageLine!!.stroke = gauge.averageColor
        dot!!.fill = gauge.barColor
    }

    // ******************** Constructors **************************************
    init {
        if (gauge.isAutoScale) gauge.calcAutoScale()
        low = gauge.maxValue
        high = gauge.minValue
        minValue = gauge.minValue
        maxValue = gauge.maxValue
        range = gauge.range
        stdDeviation = 0.0
        formatString = StringBuilder("%.").append(Integer.toString(gauge.decimals)).append("f").toString()
        locale = gauge.locale
        noOfDatapoints = gauge.averagingPeriod
        dataList = LinkedList()
        currentValueListener = InvalidationListener { o: Observable? -> handleEvents("CURRENT_VALUE") }
        averagingListener = InvalidationListener { o: Observable? -> handleEvents("AVERAGING_PERIOD") }
        for (i in 0 until noOfDatapoints) {
            dataList.add(minValue)
        }

        // To get smooth lines in the chart we need at least 4 values
        require(noOfDatapoints >= 4) { "Please increase the averaging period to a value larger than 3." }
        initGraphics()
        registerListeners()
    }
}
