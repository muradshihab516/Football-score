package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Match
import com.example.ui.theme.LightGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.StadiumBlack
import kotlin.random.Random

@Composable
fun LiveStreamViewer(
    match: Match,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showOverlay by remember { mutableStateOf(true) }
    var streamTime by remember { mutableStateOf(match.timeMinutes) }
    var soundEnabled by remember { mutableStateOf(true) }
    
    // Periodically advance seconds
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                kotlinx.coroutines.delay(4000)
                if (streamTime < 90) {
                    streamTime += 1
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .testTag("stream_viewer_container")
    ) {
        // High Density Pitch Simulation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Green pitch turf container color (light theme dynamic field tone)
            drawRect(
                color = Color(0xFF1E3F20), // Dark lush soccer field
                topLeft = Offset.Zero,
                size = size
            )

            // Draw field lines with high-contrast white transparency
            val lineStroke = Stroke(width = 2.dp.toPx())
            val dashStroke = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Center line
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(width / 2, 0f),
                end = Offset(width / 2, height),
                strokeWidth = 2.dp.toPx()
            )

            // Center circle
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = 35.dp.toPx(),
                center = Offset(width / 2, height / 2),
                style = lineStroke
            )

            // Penalty box Left
            drawRect(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(0f, height / 4),
                size = androidx.compose.ui.geometry.Size(width / 7, height / 2),
                style = lineStroke
            )

            // Penalty box Right
            drawRect(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(width - (width / 7), height / 4),
                size = androidx.compose.ui.geometry.Size(width / 7, height / 2),
                style = lineStroke
            )
            
            // Draw dummy players
            // Home team (Blue circles)
            drawCircle(Color(0xFF2196F3), 8.dp.toPx(), Offset(width * 0.3f, height * 0.4f))
            drawCircle(Color(0xFF2196F3), 8.dp.toPx(), Offset(width * 0.42f, height * 0.7f))
            drawCircle(Color(0xFF2196F3), 8.dp.toPx(), Offset(width * 0.25f, height * 0.8f))
            
            // Away team (White circles with boundary)
            drawCircle(Color(0xFFE0E0E0), 8.dp.toPx(), Offset(width * 0.7f, height * 0.5f))
            drawCircle(Color(0xFFE0E0E0), 8.dp.toPx(), Offset(width * 0.58f, height * 0.3f))
            drawCircle(Color(0xFFE0E0E0), 8.dp.toPx(), Offset(width * 0.75f, height * 0.2f))

            // The Match ball (yellow highlight)
            drawCircle(Color(0xFFADFF2F), 5.dp.toPx(), Offset(width * 0.49f, height * 0.47f))
        }

        // Live Feed Indicator
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.White, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LIVE BROADCAST",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Score info on top right
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${match.homeTeam.name.take(3).uppercase()} ${match.homeScore} - ${match.awayScore} ${match.awayTeam.name.take(3).uppercase()}",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$streamTime'",
                color = Color(0xFFADFF2F),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Tactical Overlay text
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "STREAM SPEED: 1080P • 60FPS",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (isPlaying) "TAP PITCH FOR ANALYTICAL NETWORK" else "FEED STREAM PAUSED",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Bottom control Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier.size(28.dp).testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause Stream",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { soundEnabled = !soundEnabled },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Mute",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "ScoreStream Elite Cam 1",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
