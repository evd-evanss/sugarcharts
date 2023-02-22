package com.sugarspoon.charts

import android.graphics.PointF
import android.graphics.Typeface
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sugarspoon.models.GraphEntry

object SugarCharts {

    /**
     * SugarCharts Linear
     *
     * @param entries Entrada de dados para montar o gráfico [GraphEntry]
     *
     * @param enableGrids Serve para ativar ou desativar visualização de grids. default[true]
     *
     * @param enableAnimation Serve para ativar ou desativar a animação ao desenhar a linha de dados.
     * default[false]
     *
     * @param smoothLines Serve para suavizar a linha de dados. default[true]
     *
     * @param drawValuesOnMarkers Habilita a visualização dos valores em cima dos marcadores, se [false]
     * os valores serão mostrados a esquerda.
     *
     * @param aspectRatio Tenta dimensionar o conteúdo para corresponder a uma proporção especificada.
     * default [3/2f]
     *
     * @param graphicColors Objeto com as cores do gráfico. Veja [GraphicColors]
     *
     * @param paddingValues Descreve o espaçamento a ser aplicado entre as bordas e o plano cartesiano.
     */
    @Composable
    fun Linear(
        entries: List<GraphEntry> = listOf(GraphEntry()),
        enableGrids: Boolean = true,
        enableAnimation: Boolean = false,
        smoothLines: Boolean = true,
        drawValuesOnMarkers: Boolean = true,
        aspectRatio: Float = 3 / 2f,
        graphicColors: GraphicColors = GraphicColors.defaultColors(),
        paddingValues: PaddingValues = PaddingValues(PaddingDefault)
    ) {
        Box(
            modifier = Modifier
                .background(graphicColors.backgroundColor)
                .fillMaxWidth()
        ) {
            val animationProgress = remember {
                Animatable(0f)
            }
            if (enableAnimation) {
                LaunchedEffect(
                    key1 = entries,
                    block = {
                        animationProgress.animateTo(1f, tween(3000))
                    }
                )
            }
            Spacer(
                modifier = Modifier
                    .padding(paddingValues)
                    .aspectRatio(aspectRatio)
                    .fillMaxSize()
                    .drawWithCache {
                        val path = entries.getPath(isSmooth = smoothLines, size)
                        val filledPath = entries.getPath(isSmooth = smoothLines, size)

                        filledPath.relativeLineTo(0f, size.height)
                        filledPath.lineTo(0f, size.height)
                        filledPath.close()

                        onDrawBehind {
                            drawLine(
                                color = graphicColors.axisColor,
                                start = Offset(x = 0f, 0f),
                                end = Offset(x = 0f, size.height),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = graphicColors.axisColor,
                                start = Offset(x = 0f, size.height),
                                end = Offset(x = size.width, size.height),
                                cap = StrokeCap.Round
                            )

                            if (enableGrids) {
                                drawVerticalLines(
                                    width = size.width,
                                    entries = entries,
                                    lineColor = graphicColors.gridsColor
                                )
                                drawHorizontalLines(
                                    entries = entries,
                                    height = size.height,
                                    lineColor = graphicColors.gridsColor
                                )
                            }
                            if (enableAnimation) {
                                clipRect(right = size.width * animationProgress.value) {
                                    drawPath(
                                        path, graphicColors.pathColor,
                                        style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                                    )

                                    drawPath(
                                        filledPath,
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                graphicColors.pathColor.copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        ),
                                        style = Fill
                                    )
                                }
                            } else {
                                drawPath(
                                    path, graphicColors.pathColor,
                                    style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                                )

                                drawPath(
                                    filledPath,
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            graphicColors.pathColor.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    style = Fill
                                )
                            }

                            drawMarkers(
                                data = entries,
                                markerColor = graphicColors.markerColor
                            )

                            drawTextValues(
                                data = entries,
                                textColor = graphicColors.xAxisLegendColor,
                                drawValuesOnMarkers = drawValuesOnMarkers
                            )

                            drawLegends(
                                width = size.width,
                                textColor = graphicColors.xAxisLegendColor,
                                entries = entries
                            )
                        }
                    }
            )
        }
    }
}

private fun DrawScope.drawVerticalLines(
    width: Float,
    stroke: Float = 1.dp.toPx(),
    entries: List<GraphEntry>,
    lineColor: Color
) {
    val verticalLines = entries.size
    val verticalSize = ((width) / (verticalLines + 1))

    repeat(verticalLines) { i ->
        val startX = verticalSize * (i + 1)
        drawLine(
            lineColor,
            start = Offset(startX, 0f),
            end = Offset(startX, size.height),
            strokeWidth = stroke
        )
    }
}

private fun DrawScope.drawHorizontalLines(
    height: Float,
    stroke: Float = 1.dp.toPx(),
    entries: List<GraphEntry>,
    lineColor: Color
) {
    val sectionSize = height / (entries.size + 1)

    repeat(entries.size) { i ->
        val startY = sectionSize * (i + 1)

        drawLine(
            lineColor,
            start = Offset(0f, startY),
            end = Offset(size.width, startY),
            strokeWidth = stroke
        )
    }
}

private fun DrawScope.drawMarkers(
    data: List<GraphEntry>,
    markerColor: Color
) {
    val max = data.maxBy { it.yValue }
    val scale = size.height / max.yValue
    val xAxisWidth = size.width / (data.size + 1)
    data.forEachIndexed { index, graphEntry ->
        val valueX = xAxisWidth * (index + 1)
        val Y = size.height - (graphEntry.yValue * scale)

        drawCircle(
            color = markerColor,
            radius = 8f,
            center = Offset(x = valueX, y = Y)
        )
    }
}

private fun DrawScope.drawTextValues(
    data: List<GraphEntry>,
    textColor: Color,
    drawValuesOnMarkers: Boolean
) {
    val max = data.maxBy { it.yValue }
    val scale = size.height / max.yValue
    val xAxisWidth = size.width / (data.size + 1)
    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 8.sp.toPx()
        color = textColor.toArgb()
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }
    val textByValue = mutableListOf("")

    val dataFiltered = data.distinct().toList()
    val multiplier = max.yValue / dataFiltered.size
    dataFiltered.forEachIndexed { i, graphEntry ->
        textByValue.add(
            (multiplier * i).toString()
        )
    }
    data.distinct().forEachIndexed { index, graphEntry ->
        val valueX = if (drawValuesOnMarkers) xAxisWidth * (index + 1) else -80f
        val valueY = size.height - (graphEntry.yValue * scale)

        drawIntoCanvas {
            it.nativeCanvas.drawText(
                data[index].yValue.toString(),
                valueX,
                valueY,
                textPaint
            )
        }
    }
}

private fun DrawScope.drawLegends(
    width: Float,
    entries: List<GraphEntry>,
    textColor: Color
) {
    val verticalLines = entries.size
    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 8.sp.toPx()
        color = textColor.toArgb()
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }
    val verticalSize = ((width) / (verticalLines + 1))

    repeat(verticalLines) { i ->
        val axisX = verticalSize * (i + 1)
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                entries[i].xAxisLegend.orEmpty(),
                axisX,
                size.height + 30f,
                textPaint
            )
        }
    }
}

private fun List<GraphEntry>.getPath(isSmooth: Boolean, size: Size): Path {
    return if (isSmooth) {
        generateSmoothPath(data = this, size = size)
    } else {
        generatePath(data = this, size = size)
    }
}

private fun generatePath(data: List<GraphEntry>, size: Size): Path {
    val path = Path()
    val max = data.maxBy { it.yValue }
    val scale = size.height / max.yValue
    val xAxisWidth = ((size.width) / (data.size + 1))

    data.forEachIndexed { index, graphEntry ->
        if (index == 0) {
            path.moveTo(x = 0f, y = size.height - (graphEntry.yValue))
        }
        val valueX = xAxisWidth * (index + 1)
        val valueY = size.height - graphEntry.yValue * scale
        path.lineTo(x = valueX, y = valueY)
        Log.d("SpoonGraphic", "x = $valueX y = $valueY height = ${size.height}")
    }
    return path
}

private fun generateSmoothPath(data: List<GraphEntry>, size: Size): Path {
    val path = Path()
    val max = data.maxBy { it.yValue }
    val scale = size.height / max.yValue
    val xAxisWidth = ((size.width) / (data.size + 1))

    var previousBalanceX = 0f
    var previousBalanceY = size.height
    data.forEachIndexed { index, data ->
        if (index == 0) {
            path.moveTo(x = 0f, y = size.height - (data.yValue))
        }

        val valueX = xAxisWidth * (index + 1)
        val valueY = size.height - data.yValue * scale
        // to do smooth curve graph - we use cubicTo, uncomment section below for non-curve
        val controlPoint1 = PointF((valueX + previousBalanceX) / 2f, previousBalanceY)
        val controlPoint2 = PointF((valueX + previousBalanceX) / 2f, valueY)
        path.cubicTo(
            controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y,
            valueX, valueY
        )

        previousBalanceX = valueX
        previousBalanceY = valueY
    }
    return path
}

private val Entries = listOf(
    GraphEntry(yValue = 5.44f, xAxisLegend = "fev"),
    GraphEntry(yValue = 10.22f, xAxisLegend = "mar"),
    GraphEntry(yValue = 30.00f, xAxisLegend = "abr"),
    GraphEntry(yValue = 40f, xAxisLegend = "mai"),
    GraphEntry(yValue = 41f, xAxisLegend = "jun"),
    GraphEntry(yValue = 60f, xAxisLegend = "jul"),
    GraphEntry(yValue = 40f, xAxisLegend = "ago"),
    GraphEntry(yValue = 20f, xAxisLegend = "set"),
    GraphEntry(yValue = 100f, xAxisLegend = "out"),
)

/**
 * GraphicColors
 *
 * @param backgroundColor Define a cor do plano de fundo.
 *
 * @param pathColor Define a cor da linha de dados.
 *
 * @param xAxisLegendColor Define a cor da legenda no eixo X.
 *
 * @param gridsColor Define a cor dos grids no plano cartesiano.
 *
 * @param axisColor Define a cor do eixo X e Y.
 *
 * @param markerColor Define a cor dos marcadores.
 *
 */
class GraphicColors(
    val backgroundColor: Color,
    val pathColor: Color,
    val xAxisLegendColor: Color,
    val gridsColor: Color,
    val axisColor: Color,
    val markerColor: Color,
) {

    companion object {
        fun defaultColors() = GraphicColors(
            backgroundColor = Color(0xFF6650a4),
            pathColor = Color(0xFF0AF814),
            xAxisLegendColor = Color(0xFFFFFFFF),
            gridsColor = Color(0xFF949191),
            axisColor = Color(0xFFFFFFFF),
            markerColor = Color(0xFF949191),
        )
    }
}

val PaddingDefault = 16.dp