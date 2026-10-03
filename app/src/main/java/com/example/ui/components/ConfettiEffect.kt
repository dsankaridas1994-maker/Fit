package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.OrangeAccent
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val speedY: Float,
    val speedX: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float
)

@Composable
fun ConfettiEffect(modifier: Modifier = Modifier) {
    val particles = remember {
        val colors = listOf(OrangeAccent, AmberAccent, EmeraldSuccess, CrimsonDanger, InfoBlue, Color.White)
        List(60) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                speedY = Random.nextFloat() * 0.4f + 0.6f,
                speedX = (Random.nextFloat() - 0.5f) * 0.2f,
                size = Random.nextFloat() * 10f + 6f,
                color = colors.random(),
                rotationSpeed = Random.nextFloat() * 360f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing)
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val y = ((time * p.speedY + p.initialX * 0.5f) % 1f) * size.height
            val x = (p.initialX + p.speedX * time) * size.width
            drawRect(
                color = p.color,
                topLeft = Offset(x, y),
                size = Size(p.size, p.size * 0.7f)
            )
        }
    }
}
