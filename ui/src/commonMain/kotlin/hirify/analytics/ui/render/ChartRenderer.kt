package hirify.analytics.ui.render

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import hirify.analytics.ui.ChartSeries

val chartColors = listOf(
    Color(0xFF3F51B5), // Blue
    Color(0xFFE91E63), // Pink
    Color(0xFF4CAF50), // Green
    Color(0xFFFF9800), // Orange
    Color(0xFF9C27B0)  // Purple
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun ChartRenderer(
    seriesList: List<ChartSeries>,
    modifier: Modifier = Modifier
) {
    val activeSeriesData = seriesList.filter { it.data != null && it.data.getParsedBuckets().isNotEmpty() }
    
    if (activeSeriesData.isEmpty()) {
        Box(modifier = modifier.fillMaxSize().background(Color.White), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Нет данных для отображения. Установите фильтры.")
        }
        return
    }

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize().padding(16.dp)) {
        val width = size.width
        val height = size.height

        val paddingX = 40.dp.toPx()
        val paddingY = 60.dp.toPx()

        val graphWidth = width - 2 * paddingX
        val graphHeight = height - 2 * paddingY

        // Collect all buckets to find global min/max for axes scaling
        val allKeys = activeSeriesData.flatMap { it.data!!.getParsedBuckets().keys }.distinct().sorted()
        
        val maxCount = activeSeriesData.flatMap { it.data!!.getParsedBuckets().values }.maxOrNull()?.coerceAtLeast(1) ?: 1

        val stepX = if (allKeys.size > 1) graphWidth / (allKeys.size - 1) else graphWidth
        val scaleY = graphHeight / maxCount

        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(paddingX, paddingY),
            end = Offset(paddingX, height - paddingY),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(paddingX, height - paddingY),
            end = Offset(width - paddingX, height - paddingY),
            strokeWidth = 2f
        )

        // Draw Y labels
        val ySteps = 5
        for (i in 0..ySteps) {
            val yValue = maxCount * i / ySteps
            val y = height - paddingY - (yValue * scaleY)
            drawText(
                textMeasurer = textMeasurer,
                text = yValue.toString(),
                topLeft = Offset(0f, y - 10f)
            )
            // Grid line
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(paddingX, y),
                end = Offset(width - paddingX, y),
                strokeWidth = 1f
            )
        }

        val skip = maxOf(1, (allKeys.size + 11) / 12)
        var lastDrawnYear = ""

        // Draw X labels
        allKeys.forEachIndexed { index, key ->
            val x = paddingX + index * stepX
            val parts = key.split("-")
            val year = parts.getOrNull(0) ?: ""
            val month = parts.getOrNull(1) ?: key

            if (index % skip == 0 || index == allKeys.size - 1) {
                val monthLayout = textMeasurer.measure(month)
                drawText(
                    textLayoutResult = monthLayout,
                    topLeft = Offset(x - monthLayout.size.width / 2f, height - paddingY + 8.dp.toPx())
                )
                
                if (year != lastDrawnYear && year.isNotEmpty()) {
                    val yearLayout = textMeasurer.measure(year)
                    drawText(
                        textLayoutResult = yearLayout,
                        color = Color.Gray,
                        topLeft = Offset(x - yearLayout.size.width / 2f, height - paddingY + 28.dp.toPx())
                    )
                    lastDrawnYear = year
                }
            }
        }

        // Draw lines for each series
        seriesList.forEachIndexed { seriesIndex, series ->
            val data = series.data ?: return@forEachIndexed
            val buckets = data.getParsedBuckets()
            if (buckets.isEmpty()) return@forEachIndexed
            
            val lineColor = chartColors[seriesIndex % chartColors.size]
            val path = Path()
            
            var isFirst = true
            allKeys.forEachIndexed { index, key ->
                if (buckets.containsKey(key)) {
                    val x = paddingX + index * stepX
                    val y = height - paddingY - (buckets[key]!! * scaleY)
                    
                    if (isFirst) {
                        path.moveTo(x, y)
                        isFirst = false
                    } else {
                        path.lineTo(x, y)
                    }
                    
                    drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
                }
            }
            
            if (!isFirst) {
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}
