package com.techzo.cambiazo.common.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size

@Composable
fun FancySpinner(
    modifier: Modifier = Modifier.size(42.dp),
    color: Color = Color(0xFFFFD146),
    strokeWidth: Float = 6f
) {
    val infinite = rememberInfiniteTransition(label = "spin")
    val start by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(900, easing = LinearEasing)),
        label = "start"
    )
    val sweep by infinite.animateFloat(
        initialValue = 45f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(animation = tween(900, easing = FastOutSlowInEasing)),
        label = "sweep"
    )

    Canvas(modifier = modifier) {
        drawArc(
            color = color,
            startAngle = start,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
