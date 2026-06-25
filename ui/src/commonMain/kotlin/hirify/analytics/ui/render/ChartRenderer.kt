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
import hirify.analytics.core.analytics.CountResponse

@OptIn(ExperimentalTextApi::class)
@Composable
fun ChartRenderer(
    chartData: CountResponse?,
    modifier: Modifier = Modifier
) {
    if (chartData == null || chartData.getParsedBuckets().isEmpty()) {
        Box(modifier = modifier.fillMaxSize().background(Color.White), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Нет данных для отображения. Установите фильтры.")
        }
        return
    }

    val textMeasurer = rememberTextMeasurer()
    val lineColor = MaterialTheme.colorScheme.tertiary

    Canvas(modifier = modifier.fillMaxSize().padding(16.dp)) {
        val width = size.width
        val height = size.height

        val paddingX = 40.dp.toPx()
        val paddingY = 60.dp.toPx()

        val graphWidth = width - 2 * paddingX
        val graphHeight = height - 2 * paddingY

        val sortedBuckets = chartData.getParsedBuckets().entries.sortedBy { it.key }
        val maxCount = sortedBuckets.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1

        val stepX = if (sortedBuckets.size > 1) graphWidth / (sortedBuckets.size - 1) else graphWidth
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

        // Draw line
        val path = Path()
        var lastDrawnYear = ""
        val skip = maxOf(1, (sortedBuckets.size + 11) / 12)

        sortedBuckets.forEachIndexed { index, entry ->
            val x = paddingX + index * stepX
            val y = height - paddingY - (entry.value * scaleY)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            
            // Draw point
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
            
            // Parse year and month
            val parts = entry.key.split("-")
            val year = parts.getOrNull(0) ?: ""
            val month = parts.getOrNull(1) ?: entry.key

            // Draw X label (month)
            if (index % skip == 0 || index == sortedBuckets.size - 1) {
                val monthLayout = textMeasurer.measure(month)
                drawText(
                    textLayoutResult = monthLayout,
                    topLeft = Offset(x - monthLayout.size.width / 2f, height - paddingY + 8.dp.toPx())
                )
                
                // Draw year if it changed
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
        
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
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
    }
}
