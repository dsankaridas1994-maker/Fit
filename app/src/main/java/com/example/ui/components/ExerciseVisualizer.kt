package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.ExercisePose
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.OrangeAccent
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ExerciseVisualizer(
    pose: ExercisePose,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "anime_athlete_anim")

    val cycleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cycle"
    )

    val bounceProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val progress = if (isPlaying) cycleProgress else 0.5f
    val bounce = if (isPlaying) bounceProgress else 0.5f

    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        primaryAccent.copy(alpha = 0.22f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .border(1.dp, primaryAccent.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val skinColor = Color(0xFFFFD1A4)
            val jerseyColor = primaryAccent
            val shortsColor = Color(0xFF1E1E24)
            val sneakerColor = secondaryAccent
            val glowColor = primaryAccent.copy(alpha = 0.4f)

            when (pose) {
                ExercisePose.JUMPING_JACKS -> {
                    val p = sin(progress * Math.PI).toFloat() // 0 -> 1 -> 0
                    val jumpY = -p * 22f

                    // Dynamic ground shadow
                    val shadowWidth = 90f - p * 35f
                    drawOval(
                        color = Color.Black.copy(alpha = 0.35f - p * 0.15f),
                        topLeft = Offset(cx - shadowWidth / 2f, cy + 85f),
                        size = Size(shadowWidth, 14f)
                    )

                    // Kinetic speed aura
                    if (p > 0.4f) {
                        drawLine(
                            glowColor,
                            Offset(cx - 50f, cy + 40f + jumpY),
                            Offset(cx - 50f, cy + 80f),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            glowColor,
                            Offset(cx + 50f, cy + 40f + jumpY),
                            Offset(cx + 50f, cy + 80f),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }

                    val torsoY = cy - 10f + jumpY
                    // Head & Athletic Sweatband
                    drawCircle(skinColor, 18f, Offset(cx, torsoY - 48f))
                    // Sweatband
                    drawRoundRect(
                        color = primaryAccent,
                        topLeft = Offset(cx - 18f, torsoY - 56f),
                        size = Size(36f, 9f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Athletic Torso (Muscular V-shape)
                    val torsoPath = Path().apply {
                        moveTo(cx - 22f, torsoY - 30f) // Left shoulder
                        lineTo(cx + 22f, torsoY - 30f) // Right shoulder
                        lineTo(cx + 14f, torsoY + 14f) // Right hip
                        lineTo(cx - 14f, torsoY + 14f) // Left hip
                        close()
                    }
                    drawPath(torsoPath, color = jerseyColor)

                    // Compression Shorts
                    val shortsPath = Path().apply {
                        moveTo(cx - 14f, torsoY + 14f)
                        lineTo(cx + 14f, torsoY + 14f)
                        lineTo(cx + 18f + p * 8f, torsoY + 36f)
                        lineTo(cx - 18f - p * 8f, torsoY + 36f)
                        close()
                    }
                    drawPath(shortsPath, color = shortsColor)

                    // Muscular Arms (Dual-segment: Delts/Biceps + Forearms)
                    val armAngle = -30f + p * 160f // -30 down to 130 overhead
                    val leftArmRad = Math.toRadians((180f + armAngle).toDouble())
                    val rightArmRad = Math.toRadians((-armAngle).toDouble())

                    // Left Arm
                    val leftElbow = Offset(
                        cx - 22f + cos(leftArmRad).toFloat() * 30f,
                        torsoY - 26f - sin(leftArmRad).toFloat() * 30f
                    )
                    val leftHand = Offset(
                        leftElbow.x + cos(leftArmRad + 0.2).toFloat() * 26f,
                        leftElbow.y - sin(leftArmRad + 0.2).toFloat() * 26f
                    )
                    drawLine(skinColor, Offset(cx - 20f, torsoY - 26f), leftElbow, strokeWidth = 10f, cap = StrokeCap.Round)
                    drawLine(jerseyColor, leftElbow, leftHand, strokeWidth = 8f, cap = StrokeCap.Round)
                    drawCircle(secondaryAccent, 6f, leftHand)

                    // Right Arm
                    val rightElbow = Offset(
                        cx + 22f + cos(rightArmRad).toFloat() * 30f,
                        torsoY - 26f + sin(rightArmRad).toFloat() * 30f
                    )
                    val rightHand = Offset(
                        rightElbow.x + cos(rightArmRad - 0.2).toFloat() * 26f,
                        rightElbow.y + sin(rightArmRad - 0.2).toFloat() * 26f
                    )
                    drawLine(skinColor, Offset(cx + 20f, torsoY - 26f), rightElbow, strokeWidth = 10f, cap = StrokeCap.Round)
                    drawLine(jerseyColor, rightElbow, rightHand, strokeWidth = 8f, cap = StrokeCap.Round)
                    drawCircle(secondaryAccent, 6f, rightHand)

                    // Muscular Legs (Thighs + Calves + Sneakers)
                    val legSpread = 12f + p * 38f
                    val leftKnee = Offset(cx - legSpread * 0.7f, torsoY + 56f)
                    val leftFoot = Offset(cx - legSpread, torsoY + 84f - jumpY)
                    drawLine(skinColor, Offset(cx - 10f, torsoY + 34f), leftKnee, strokeWidth = 11f, cap = StrokeCap.Round)
                    drawLine(skinColor, leftKnee, leftFoot, strokeWidth = 9f, cap = StrokeCap.Round)
                    // Sneaker
                    drawRoundRect(sneakerColor, Offset(leftFoot.x - 14f, leftFoot.y - 4f), Size(22f, 10f), CornerRadius(4f, 4f))

                    val rightKnee = Offset(cx + legSpread * 0.7f, torsoY + 56f)
                    val rightFoot = Offset(cx + legSpread, torsoY + 84f - jumpY)
                    drawLine(skinColor, Offset(cx + 10f, torsoY + 34f), rightKnee, strokeWidth = 11f, cap = StrokeCap.Round)
                    drawLine(skinColor, rightKnee, rightFoot, strokeWidth = 9f, cap = StrokeCap.Round)
                    // Sneaker
                    drawRoundRect(sneakerColor, Offset(rightFoot.x - 8f, rightFoot.y - 4f), Size(22f, 10f), CornerRadius(4f, 4f))
                }

                ExercisePose.SQUATS -> {
                    val p = sin(progress * Math.PI).toFloat() // 0 = stand, 1 = deep squat
                    val squatDrop = p * 45f

                    // Shadow
                    drawOval(
                        color = Color.Black.copy(alpha = 0.4f),
                        topLeft = Offset(cx - 50f - p * 15f, cy + 85f),
                        size = Size(100f + p * 30f, 16f)
                    )

                    val hipY = cy + squatDrop * 0.7f
                    val shoulderY = cy - 45f + squatDrop

                    // Head & Visor
                    drawCircle(skinColor, 18f, Offset(cx + 4f, shoulderY - 20f))
                    drawRoundRect(
                        primaryAccent,
                        Offset(cx - 8f, shoulderY - 26f),
                        Size(28f, 8f),
                        CornerRadius(4f, 4f)
                    )

                    // Torso angled slightly forward for biomechanic realism
                    val torsoPath = Path().apply {
                        moveTo(cx - 16f, shoulderY)
                        lineTo(cx + 16f, shoulderY)
                        lineTo(cx + 8f - p * 16f, hipY + 10f)
                        lineTo(cx - 16f - p * 16f, hipY + 10f)
                        close()
                    }
                    drawPath(torsoPath, color = jerseyColor)

                    // Arms extended forward in counter-balance
                    val armExtend = 45f + p * 15f
                    drawLine(skinColor, Offset(cx + 12f, shoulderY + 4f), Offset(cx + armExtend, shoulderY + 4f), strokeWidth = 10f, cap = StrokeCap.Round)
                    drawCircle(secondaryAccent, 6f, Offset(cx + armExtend + 8f, shoulderY + 4f))

                    // Deep Squat Leg Mechanics: Hips back, knees forward at 90 deg
                    val hipPos = Offset(cx - 8f - p * 16f, hipY + 10f)
                    val kneePos = Offset(cx + 26f, cy + 30f + squatDrop * 0.3f)
                    val footPos = Offset(cx + 12f, cy + 84f)

                    // Thigh (Heavy quad)
                    drawLine(shortsColor, hipPos, kneePos, strokeWidth = 15f, cap = StrokeCap.Round)
                    // Calf (Shin vertical)
                    drawLine(skinColor, kneePos, footPos, strokeWidth = 12f, cap = StrokeCap.Round)
                    // Sneaker
                    drawRoundRect(sneakerColor, Offset(footPos.x - 14f, footPos.y - 4f), Size(30f, 12f), CornerRadius(4f, 4f))
                }

                ExercisePose.PUSH_UPS -> {
                    val p = sin(progress * Math.PI).toFloat() // 0 = high plank, 1 = chest floor
                    val chestDrop = p * 34f

                    // Floor Line & Shadow
                    drawLine(Color(0x33FFFFFF), Offset(cx - 95f, cy + 45f), Offset(cx + 95f, cy + 45f), strokeWidth = 3f)
                    drawOval(
                        color = Color.Black.copy(alpha = 0.4f),
                        topLeft = Offset(cx - 80f, cy + 40f),
                        size = Size(160f, 14f)
                    )

                    val feetPos = Offset(cx - 68f, cy + 36f)
                    val shoulderPos = Offset(cx + 38f, cy - 2f + chestDrop)

                    // Head facing forward
                    drawCircle(skinColor, 17f, Offset(cx + 56f, shoulderY(cy - 2f + chestDrop)))
                    drawRoundRect(primaryAccent, Offset(cx + 50f, shoulderY(cy - 8f + chestDrop)), Size(20f, 7f), CornerRadius(3f, 3f))

                    // Muscular Torso & Glutes line
                    drawLine(jerseyColor, feetPos, shoulderPos, strokeWidth = 16f, cap = StrokeCap.Round)

                    // Muscular Arms (Pectoral flexion)
                    val handPos = Offset(cx + 34f, cy + 42f)
                    val elbowPos = Offset(cx + 18f - p * 16f, cy + 16f + chestDrop * 0.7f)
                    drawLine(skinColor, shoulderPos, elbowPos, strokeWidth = 12f, cap = StrokeCap.Round)
                    drawLine(secondaryAccent, elbowPos, handPos, strokeWidth = 10f, cap = StrokeCap.Round)

                    // Sneaker on toe pivot
                    drawRoundRect(sneakerColor, Offset(feetPos.x - 12f, feetPos.y - 6f), Size(20f, 12f), CornerRadius(4f, 4f))
                }

                ExercisePose.PLANK -> {
                    val breath = bounce * 5f
                    // Core thermal heat aura
                    drawCircle(
                        Brush.radialGradient(listOf(primaryAccent.copy(alpha = 0.4f), Color.Transparent)),
                        radius = 55f + breath * 3f,
                        center = Offset(cx - 10f, cy + 20f)
                    )

                    // Floor Line
                    drawLine(Color(0x33FFFFFF), Offset(cx - 95f, cy + 45f), Offset(cx + 95f, cy + 45f), strokeWidth = 3f)

                    val feetPos = Offset(cx - 70f, cy + 36f)
                    val shoulderPos = Offset(cx + 36f, cy + 12f + breath)

                    // Head
                    drawCircle(skinColor, 17f, Offset(cx + 56f, cy + 6f + breath))
                    drawRoundRect(primaryAccent, Offset(cx + 52f, cy + breath), Size(18f, 6f), CornerRadius(3f, 3f))

                    // Straight Isometric Spine
                    drawLine(jerseyColor, feetPos, shoulderPos, strokeWidth = 18f, cap = StrokeCap.Round)

                    // Forearms resting flat on mat
                    val elbowPos = Offset(cx + 36f, cy + 42f)
                    val fistPos = Offset(cx + 58f, cy + 42f)
                    drawLine(skinColor, shoulderPos, elbowPos, strokeWidth = 12f, cap = StrokeCap.Round)
                    drawLine(secondaryAccent, elbowPos, fistPos, strokeWidth = 11f, cap = StrokeCap.Round)

                    // Sneakers locked in place
                    drawRoundRect(sneakerColor, Offset(feetPos.x - 12f, feetPos.y - 6f), Size(20f, 12f), CornerRadius(4f, 4f))
                }

                ExercisePose.CRUNCHES -> {
                    val p = sin(progress * Math.PI).toFloat() // 0 = flat, 1 = curled
                    val curlAngle = p * 40f

                    // Mat / Floor
                    drawLine(Color(0x33FFFFFF), Offset(cx - 95f, cy + 50f), Offset(cx + 95f, cy + 50f), strokeWidth = 3f)

                    // Hips on floor
                    val hipPos = Offset(cx - 8f, cy + 38f)
                    // Knees bent up
                    val kneePos = Offset(cx + 34f, cy - 2f)
                    val footPos = Offset(cx + 56f, cy + 46f)
                    drawLine(shortsColor, hipPos, kneePos, strokeWidth = 16f, cap = StrokeCap.Round)
                    drawLine(skinColor, kneePos, footPos, strokeWidth = 12f, cap = StrokeCap.Round)
                    drawRoundRect(sneakerColor, Offset(footPos.x - 6f, footPos.y - 6f), Size(24f, 12f), CornerRadius(4f, 4f))

                    // Torso curling towards knees
                    val shoulderPos = Offset(cx - 44f + p * 18f, cy + 34f - curlAngle)
                    drawLine(jerseyColor, hipPos, shoulderPos, strokeWidth = 18f, cap = StrokeCap.Round)

                    // Head & Hands behind neck
                    drawCircle(skinColor, 17f, Offset(shoulderPos.x - 14f, shoulderPos.y - 12f))
                    drawLine(secondaryAccent, shoulderPos, Offset(shoulderPos.x - 8f, shoulderPos.y - 20f), strokeWidth = 8f, cap = StrokeCap.Round)
                }

                ExercisePose.MOUNTAIN_CLIMBERS -> {
                    val p = sin(progress * Math.PI * 2f).toFloat() // -1 to 1

                    // Floor Line
                    drawLine(Color(0x33FFFFFF), Offset(cx - 95f, cy + 45f), Offset(cx + 95f, cy + 45f), strokeWidth = 3f)

                    val handPos = Offset(cx + 42f, cy + 42f)
                    val shoulderPos = Offset(cx + 40f, cy + 4f)
                    val hipPos = Offset(cx - 20f, cy + 12f)

                    // Head
                    drawCircle(skinColor, 17f, Offset(cx + 58f, cy - 4f))
                    drawRoundRect(primaryAccent, Offset(cx + 54f, cy - 10f), Size(18f, 6f), CornerRadius(3f, 3f))

                    // Torso
                    drawLine(jerseyColor, hipPos, shoulderPos, strokeWidth = 17f, cap = StrokeCap.Round)
                    // Arms supporting plank
                    drawLine(skinColor, shoulderPos, handPos, strokeWidth = 12f, cap = StrokeCap.Round)

                    // Leg 1 (drives deep toward chest when p > 0)
                    val knee1 = Offset(cx - 8f + maxOf(0f, p) * 38f, cy + 24f)
                    val foot1 = Offset(knee1.x - 22f, cy + 42f)
                    drawLine(shortsColor, hipPos, knee1, strokeWidth = 14f, cap = StrokeCap.Round)
                    drawLine(skinColor, knee1, foot1, strokeWidth = 10f, cap = StrokeCap.Round)
                    drawRoundRect(sneakerColor, Offset(foot1.x - 10f, foot1.y - 4f), Size(20f, 10f), CornerRadius(3f, 3f))

                    // Leg 2 (drives deep toward chest when p < 0)
                    val knee2 = Offset(cx - 8f + maxOf(0f, -p) * 38f, cy + 24f)
                    val foot2 = Offset(knee2.x - 22f, cy + 42f)
                    drawLine(shortsColor.copy(alpha = 0.6f), hipPos, knee2, strokeWidth = 14f, cap = StrokeCap.Round)
                    drawLine(skinColor.copy(alpha = 0.6f), knee2, foot2, strokeWidth = 10f, cap = StrokeCap.Round)
                    drawRoundRect(sneakerColor.copy(alpha = 0.6f), Offset(foot2.x - 10f, foot2.y - 4f), Size(20f, 10f), CornerRadius(3f, 3f))
                }

                ExercisePose.HIGH_KNEES -> {
                    val p = sin(progress * Math.PI * 2f).toFloat() // -1 to 1
                    val bounceY = bounce * 14f

                    // Shadow
                    drawOval(
                        color = Color.Black.copy(alpha = 0.35f),
                        topLeft = Offset(cx - 45f, cy + 85f),
                        size = Size(90f, 14f)
                    )

                    val torsoY = cy - 20f + bounceY
                    // Head & Visor
                    drawCircle(skinColor, 18f, Offset(cx, torsoY - 45f))
                    drawRoundRect(primaryAccent, Offset(cx - 16f, torsoY - 52f), Size(32f, 8f), CornerRadius(4f, 4f))

                    // Torso
                    val torsoPath = Path().apply {
                        moveTo(cx - 20f, torsoY - 26f)
                        lineTo(cx + 20f, torsoY - 26f)
                        lineTo(cx + 14f, torsoY + 18f)
                        lineTo(cx - 14f, torsoY + 18f)
                        close()
                    }
                    drawPath(torsoPath, color = jerseyColor)

                    // Pumping Arms in athletic sprint motion
                    val leftArmY = torsoY - 14f + p * 24f
                    val rightArmY = torsoY - 14f - p * 24f
                    drawLine(skinColor, Offset(cx - 18f, torsoY - 22f), Offset(cx - 36f, leftArmY), strokeWidth = 10f, cap = StrokeCap.Round)
                    drawCircle(secondaryAccent, 6f, Offset(cx - 36f, leftArmY))
                    drawLine(skinColor, Offset(cx + 18f, torsoY - 22f), Offset(cx + 36f, rightArmY), strokeWidth = 10f, cap = StrokeCap.Round)
                    drawCircle(secondaryAccent, 6f, Offset(cx + 36f, rightArmY))

                    // Alternating 90-degree High Knee Drive
                    if (p > 0) {
                        // Left knee high
                        val kneeHigh = Offset(cx + 28f, torsoY + 16f)
                        val footHigh = Offset(cx + 26f, torsoY + 50f)
                        drawLine(shortsColor, Offset(cx + 10f, torsoY + 18f), kneeHigh, strokeWidth = 15f, cap = StrokeCap.Round)
                        drawLine(skinColor, kneeHigh, footHigh, strokeWidth = 11f, cap = StrokeCap.Round)
                        drawRoundRect(sneakerColor, Offset(footHigh.x - 8f, footHigh.y - 4f), Size(24f, 11f), CornerRadius(4f, 4f))

                        // Right leg grounded / pushing
                        val footDown = Offset(cx - 14f, cy + 84f)
                        drawLine(shortsColor, Offset(cx - 10f, torsoY + 18f), Offset(cx - 12f, torsoY + 52f), strokeWidth = 15f, cap = StrokeCap.Round)
                        drawLine(skinColor, Offset(cx - 12f, torsoY + 52f), footDown, strokeWidth = 11f, cap = StrokeCap.Round)
                        drawRoundRect(sneakerColor, Offset(footDown.x - 12f, footDown.y - 4f), Size(24f, 11f), CornerRadius(4f, 4f))
                    } else {
                        // Right knee high
                        val kneeHigh = Offset(cx - 28f, torsoY + 16f)
                        val footHigh = Offset(cx - 26f, torsoY + 50f)
                        drawLine(shortsColor, Offset(cx - 10f, torsoY + 18f), kneeHigh, strokeWidth = 15f, cap = StrokeCap.Round)
                        drawLine(skinColor, kneeHigh, footHigh, strokeWidth = 11f, cap = StrokeCap.Round)
                        drawRoundRect(sneakerColor, Offset(footHigh.x - 14f, footHigh.y - 4f), Size(24f, 11f), CornerRadius(4f, 4f))

                        // Left leg grounded
                        val footDown = Offset(cx + 14f, cy + 84f)
                        drawLine(shortsColor, Offset(cx + 10f, torsoY + 18f), Offset(cx + 12f, torsoY + 52f), strokeWidth = 15f, cap = StrokeCap.Round)
                        drawLine(skinColor, Offset(cx + 12f, torsoY + 52f), footDown, strokeWidth = 11f, cap = StrokeCap.Round)
                        drawRoundRect(sneakerColor, Offset(footDown.x - 6f, footDown.y - 4f), Size(24f, 11f), CornerRadius(4f, 4f))
                    }
                }

                ExercisePose.LUNGES -> {
                    val p = sin(progress * Math.PI).toFloat()
                    val lungeDepth = p * 30f

                    // Shadow
                    drawOval(
                        color = Color.Black.copy(alpha = 0.35f),
                        topLeft = Offset(cx - 50f, cy + 85f),
                        size = Size(100f, 14f)
                    )

                    val torsoY = cy - 26f + lungeDepth
                    // Head
                    drawCircle(skinColor, 18f, Offset(cx, torsoY - 44f))
                    drawRoundRect(primaryAccent, Offset(cx - 14f, torsoY - 50f), Size(28f, 7f), CornerRadius(3f, 3f))

                    // Vertical Torso
                    val torsoPath = Path().apply {
                        moveTo(cx - 18f, torsoY - 26f)
                        lineTo(cx + 18f, torsoY - 26f)
                        lineTo(cx + 12f, torsoY + 18f)
                        lineTo(cx - 12f, torsoY + 18f)
                        close()
                    }
                    drawPath(torsoPath, color = jerseyColor)

                    // Front Knee (bent at 90)
                    val frontKnee = Offset(cx + 36f, cy + 36f + lungeDepth)
                    val frontFoot = Offset(cx + 36f, cy + 82f)
                    drawLine(shortsColor, Offset(cx + 10f, torsoY + 18f), frontKnee, strokeWidth = 15f, cap = StrokeCap.Round)
                    drawLine(skinColor, frontKnee, frontFoot, strokeWidth = 11f, cap = StrokeCap.Round)
                    drawRoundRect(sneakerColor, Offset(frontFoot.x - 8f, frontFoot.y - 4f), Size(26f, 11f), CornerRadius(4f, 4f))

                    // Rear Knee (dropping close to ground)
                    val rearKnee = Offset(cx - 28f, cy + 58f + lungeDepth * 0.7f)
                    val rearFoot = Offset(cx - 50f, cy + 78f)
                    drawLine(shortsColor, Offset(cx - 10f, torsoY + 18f), rearKnee, strokeWidth = 15f, cap = StrokeCap.Round)
                    drawLine(skinColor, rearKnee, rearFoot, strokeWidth = 11f, cap = StrokeCap.Round)
                    drawRoundRect(sneakerColor, Offset(rearFoot.x - 14f, rearFoot.y - 4f), Size(24f, 10f), CornerRadius(4f, 4f))
                }

                ExercisePose.BURPEES -> {
                    val p = progress // 0 to 1 loop
                    // 0..0.3 squat drop, 0.3..0.6 kick back to plank, 0.6..0.8 jump forward, 0.8..1.0 sky leap
                    val isLeap = p > 0.8f
                    val leapOffset = if (isLeap) -(p - 0.8f) * 60f else 0f
                    val isPlank = p in 0.25f..0.7f

                    // Shadow shrinks on leap
                    val shadowAlpha = if (isLeap) 0.15f else 0.4f
                    val shadowScale = if (isLeap) 0.5f else 1f
                    drawOval(
                        color = Color.Black.copy(alpha = shadowAlpha),
                        topLeft = Offset(cx - 45f * shadowScale, cy + 85f),
                        size = Size(90f * shadowScale, 14f)
                    )

                    if (isPlank) {
                        // High plank phase
                        val feet = Offset(cx - 65f, cy + 40f)
                        val shoulder = Offset(cx + 35f, cy + 12f)
                        drawCircle(skinColor, 16f, Offset(cx + 52f, cy + 6f))
                        drawLine(jerseyColor, feet, shoulder, strokeWidth = 16f, cap = StrokeCap.Round)
                        drawLine(skinColor, shoulder, Offset(cx + 35f, cy + 42f), strokeWidth = 11f, cap = StrokeCap.Round)
                        drawRoundRect(sneakerColor, Offset(feet.x - 10f, feet.y - 4f), Size(20f, 10f), CornerRadius(3f, 3f))
                    } else {
                        // Upright or Soaring Leap phase
                        val athleteY = cy - 20f + leapOffset
                        drawCircle(skinColor, 18f, Offset(cx, athleteY - 45f))
                        drawRoundRect(primaryAccent, Offset(cx - 16f, athleteY - 52f), Size(32f, 8f), CornerRadius(4f, 4f))

                        val torsoPath = Path().apply {
                            moveTo(cx - 20f, athleteY - 26f)
                            lineTo(cx + 20f, athleteY - 26f)
                            lineTo(cx + 14f, athleteY + 18f)
                            lineTo(cx - 14f, athleteY + 18f)
                            close()
                        }
                        drawPath(torsoPath, color = jerseyColor)

                        // Arms reaching overhead if leap
                        val armY = if (isLeap) athleteY - 60f else athleteY + 10f
                        drawLine(skinColor, Offset(cx - 18f, athleteY - 22f), Offset(cx - 30f, armY), strokeWidth = 10f, cap = StrokeCap.Round)
                        drawCircle(secondaryAccent, 6f, Offset(cx - 30f, armY))
                        drawLine(skinColor, Offset(cx + 18f, athleteY - 22f), Offset(cx + 30f, armY), strokeWidth = 10f, cap = StrokeCap.Round)
                        drawCircle(secondaryAccent, 6f, Offset(cx + 30f, armY))

                        // Legs
                        val footY = if (isLeap) athleteY + 70f else cy + 82f
                        drawLine(shortsColor, Offset(cx - 10f, athleteY + 18f), Offset(cx - 12f, athleteY + 46f), strokeWidth = 14f, cap = StrokeCap.Round)
                        drawLine(skinColor, Offset(cx - 12f, athleteY + 46f), Offset(cx - 14f, footY), strokeWidth = 10f, cap = StrokeCap.Round)
                        drawRoundRect(sneakerColor, Offset(cx - 24f, footY - 4f), Size(22f, 10f), CornerRadius(3f, 3f))

                        drawLine(shortsColor, Offset(cx + 10f, athleteY + 18f), Offset(cx + 12f, athleteY + 46f), strokeWidth = 14f, cap = StrokeCap.Round)
                        drawLine(skinColor, Offset(cx + 12f, athleteY + 46f), Offset(cx + 14f, footY), strokeWidth = 10f, cap = StrokeCap.Round)
                        drawRoundRect(sneakerColor, Offset(cx + 4f, footY - 4f), Size(22f, 10f), CornerRadius(3f, 3f))
                    }
                }

                ExercisePose.REST -> {
                    val pulse = bounce * 18f
                    // Zen aura concentric circles
                    drawCircle(
                        Brush.radialGradient(listOf(EmeraldSuccess.copy(alpha = 0.45f), Color.Transparent)),
                        radius = 75f + pulse,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = EmeraldSuccess.copy(alpha = 0.7f),
                        radius = 42f + pulse * 0.4f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 3f)
                    )

                    // Standing peaceful posture
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - 40f, cy + 85f), Size(80f, 12f))

                    // Head with gentle breathing tilt
                    drawCircle(skinColor, 18f, Offset(cx, cy - 50f + pulse * 0.15f))
                    drawRoundRect(EmeraldSuccess, Offset(cx - 16f, cy - 58f + pulse * 0.15f), Size(32f, 7f), CornerRadius(3f, 3f))

                    // Torso
                    val torsoPath = Path().apply {
                        moveTo(cx - 18f, cy - 30f + pulse * 0.15f)
                        lineTo(cx + 18f, cy - 30f + pulse * 0.15f)
                        lineTo(cx + 12f, cy + 14f)
                        lineTo(cx - 12f, cy + 14f)
                        close()
                    }
                    drawPath(torsoPath, color = EmeraldSuccess)

                    // Hands resting relaxed
                    drawLine(skinColor, Offset(cx - 18f, cy - 24f), Offset(cx - 24f, cy + 10f), strokeWidth = 9f, cap = StrokeCap.Round)
                    drawLine(skinColor, Offset(cx + 18f, cy - 24f), Offset(cx + 24f, cy + 10f), strokeWidth = 9f, cap = StrokeCap.Round)

                    // Legs
                    drawLine(shortsColor, Offset(cx - 8f, cy + 14f), Offset(cx - 10f, cy + 50f), strokeWidth = 14f, cap = StrokeCap.Round)
                    drawLine(skinColor, Offset(cx - 10f, cy + 50f), Offset(cx - 12f, cy + 84f), strokeWidth = 10f, cap = StrokeCap.Round)
                    drawRoundRect(sneakerColor, Offset(cx - 20f, cy + 80f), Size(20f, 10f), CornerRadius(3f, 3f))

                    drawLine(shortsColor, Offset(cx + 8f, cy + 14f), Offset(cx + 10f, cy + 50f), strokeWidth = 14f, cap = StrokeCap.Round)
                    drawLine(skinColor, Offset(cx + 10f, cy + 50f), Offset(cx + 12f, cy + 84f), strokeWidth = 10f, cap = StrokeCap.Round)
                    drawRoundRect(sneakerColor, Offset(cx + 2f, cy + 80f), Size(20f, 10f), CornerRadius(3f, 3f))
                }
            }
        }
    }
}

private fun shoulderY(y: Float): Float = y - 8f
