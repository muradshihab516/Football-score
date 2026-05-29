package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.FootballMatch
import com.example.ui.theme.PitchDarkLine
import com.example.ui.theme.PitchGreen

@Composable
fun FootballPitchTracker(
    match: FootballMatch,
    ballX: Float,
    ballY: Float,
    phaseText: String,
    modifier: Modifier = Modifier
) {
    // Smoothen ball movement animations via Jetpack Compose State Animators
    val animatedBallX by animateFloatAsState(
        targetValue = ballX,
        animationSpec = tween(durationMillis = 600)
    )
    val animatedBallY by animateFloatAsState(
        targetValue = ballY,
        animationSpec = tween(durationMillis = 600)
    )

    Column(
        modifier = modifier
            .testTag("football_pitch_tracker")
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, PitchDarkLine, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Tracker Title bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "LIVE TACTICAL STREAM",
                    style = MaterialTheme.typography.labelSmall,
                    color = PitchGreen,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = phaseText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = "Soccer live icon",
                tint = PitchGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        // Draw Interactive 2D Pitch Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF07140D),
                            Color(0xFF0F2618)
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(1.dp, Color(0xFF225235), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Draw field markings via custom Canvas Drawing APIs
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val lineColor = Color(0x3500FF66)
                val lineStroke = 1.dp.toPx()

                // 1. Boundary lines
                drawRect(
                    color = lineColor,
                    topLeft = Offset(10.dp.toPx(), 10.dp.toPx()),
                    size = Size(w - 20.dp.toPx(), h - 20.dp.toPx()),
                    style = Stroke(width = lineStroke)
                )

                // 2. Midfield Line
                drawLine(
                    color = lineColor,
                    start = Offset(w / 2, 10.dp.toPx()),
                    end = Offset(w / 2, h - 10.dp.toPx()),
                    strokeWidth = lineStroke
                )

                // 3. Center Circle
                drawCircle(
                    color = lineColor,
                    radius = 32.dp.toPx(),
                    center = Offset(w / 2, h / 2),
                    style = Stroke(width = lineStroke)
                )
                drawCircle(
                    color = lineColor,
                    radius = 2.dp.toPx(),
                    center = Offset(w / 2, h / 2)
                )

                // 4. Left Penalty Box
                drawRect(
                    color = lineColor,
                    topLeft = Offset(10.dp.toPx(), h / 2 - 40.dp.toPx()),
                    size = Size(40.dp.toPx(), 80.dp.toPx()),
                    style = Stroke(width = lineStroke)
                )
                // Left Goal area box
                drawRect(
                    color = lineColor,
                    topLeft = Offset(10.dp.toPx(), h / 2 - 18.dp.toPx()),
                    size = Size(14.dp.toPx(), 36.dp.toPx()),
                    style = Stroke(width = lineStroke)
                )

                // 5. Right Penalty Box
                drawRect(
                    color = lineColor,
                    topLeft = Offset(w - 50.dp.toPx(), h / 2 - 40.dp.toPx()),
                    size = Size(40.dp.toPx(), 80.dp.toPx()),
                    style = Stroke(width = lineStroke)
                )
                // Right Goal area box
                drawRect(
                    color = lineColor,
                    topLeft = Offset(w - 24.dp.toPx(), h / 2 - 18.dp.toPx()),
                    size = Size(14.dp.toPx(), 36.dp.toPx()),
                    style = Stroke(width = lineStroke)
                )
            }

            // Draw Home Team Core Formations representation overlay dots (Left/Right depending on attacking side)
            // Draw Animated glowing Football tracking node
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Ball Position
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val widthPx = maxWidth
                    val heightPx = maxHeight

                    // Place team indicator labels
                    Text(
                        text = match.homeTeam.shortName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(android.graphics.Color.parseColor(match.homeTeam.primaryColorHex)),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 24.dp)
                    )

                    Text(
                        text = match.awayTeam.shortName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(android.graphics.Color.parseColor(match.awayTeam.primaryColorHex)),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 24.dp)
                    )

                    // Draw the animated soccer ball node and halo ring
                    val ballOffsetOffsetX = (animatedBallX * (widthPx.value - 40f)).dp
                    val ballOffsetOffsetY = (animatedBallY * (heightPx.value - 40f)).dp

                    Box(
                        modifier = Modifier
                            .offset(x = ballOffsetOffsetX + 20.dp, y = ballOffsetOffsetY + 20.dp)
                            .size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulsing outer flare ring
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(10.dp),
                            color = PitchGreen.copy(alpha = 0.35f),
                            content = {}
                        )
                        // Soccer core dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        // Live stats ticker bar right on field base
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${match.homeTeam.name} possession: ${match.stats.possessionHome}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Shots: ${match.stats.shotsHome} - ${match.stats.shotsAway}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${match.awayTeam.name}: ${match.stats.possessionAway}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
