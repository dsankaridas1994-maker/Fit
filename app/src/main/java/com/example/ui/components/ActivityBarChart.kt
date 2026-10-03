package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyActivity
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.OrangeAccent

@Composable
fun ActivityBarChart(
    activities: List<DailyActivity>,
    modifier: Modifier = Modifier
) {
    if (activities.isEmpty()) {
        Box(
            modifier = modifier.height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No workout activity recorded yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val maxMinutes = maxOf(30, activities.maxOfOrNull { it.minutes } ?: 30)
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier) {
        // Selected tooltip if any
        val selected = selectedIndex?.let { activities.getOrNull(it) }
        Text(
            text = if (selected != null) "${selected.date}: ${selected.minutes} mins" else "Tap a bar to inspect details",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected != null) OrangeAccent else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            val isSingleWeek = activities.size <= 7
            val barWidth = if (isSingleWeek) 28.dp else 7.dp

            activities.forEachIndexed { index, activity ->
                val isToday = index == activities.lastIndex
                val isSelected = selectedIndex == index
                val ratio = (activity.minutes.toFloat() / maxMinutes.toFloat()).coerceIn(0.04f, 1f)

                val animatedRatio by animateFloatAsState(
                    targetValue = ratio,
                    animationSpec = tween(durationMillis = 600, delayMillis = index * 20),
                    label = "bar_$index"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { selectedIndex = index },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (isSingleWeek && activity.minutes > 0) {
                        Text(
                            text = "${activity.minutes}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Bar
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .fillMaxHeight(animatedRatio)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                when {
                                    isSelected -> Color.White
                                    isToday -> EmeraldSuccess
                                    activity.minutes > 0 -> OrangeAccent
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                }
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Label (only show for all in 7-day mode, or every few days in 30-day mode)
                    if (isSingleWeek || index % 5 == 0 || isToday) {
                        Text(
                            text = activity.dayLabel,
                            fontSize = 9.sp,
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isToday) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
