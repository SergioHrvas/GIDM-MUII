package com.pandemiagame.org.ui.screens.profile.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WinRatePieChart(won: Int, total: Int) {
    val percentage = if (total > 0) (won.toFloat() / total.toFloat()) else 0f
    val colorWon = Color.Green
    val colorLost = Color.LightGray

    Canvas(modifier = Modifier.size(150.dp)) {
        // Fondo (parte perdida)
        drawArc(
            color = colorLost,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 20f, cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        // Parte ganada
        drawArc(
            color = colorWon,
            startAngle = -90f,
            sweepAngle = 360f * percentage,
            useCenter = false,
            style = Stroke(width = 20f, cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
    }

    // Texto del porcentaje
    Text(
        text = "${(percentage * 100).toInt()}%",
        modifier = Modifier.padding(top = 8.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}