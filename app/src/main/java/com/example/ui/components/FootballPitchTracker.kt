package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Match
import com.example.ui.theme.LightGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PitchGreen

@Composable
fun FootballPitchTracker(
    match: Match,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("pitch_tracker_container"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = match.homeTeam.name.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = LightGrey,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Formation: ${match.formationHome}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGrey
                )
            }
            Text(
                text = "VS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = PitchGreen
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = match.awayTeam.name.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = LightGrey,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Formation: ${match.formationAway}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGrey
                )
            }
        }

        // The Tactical Football Field Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2E6B3E))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Pitch outer perimeter line
                drawRect(
                    color = Color.White.copy(alpha = 0.3f),
                    topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(w - 16.dp.toPx(), h - 16.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Halfway line
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(w / 2, 8.dp.toPx()),
                    end = Offset(w / 2, h - 8.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx()
                )

                // Center circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = 28.dp.toPx(),
                    center = Offset(w / 2, h / 2),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Goal Area Left
                drawRect(
                    color = Color.White.copy(alpha = 0.3f),
                    topLeft = Offset(8.dp.toPx(), h / 3),
                    size = androidx.compose.ui.geometry.Size(25.dp.toPx(), h / 3),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Goal Area Right
                drawRect(
                    color = Color.White.copy(alpha = 0.3f),
                    topLeft = Offset(w - 8.dp.toPx() - 25.dp.toPx(), h / 3),
                    size = androidx.compose.ui.geometry.Size(25.dp.toPx(), h / 3),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Simulated Tactical Formations Nodes (4-3-3 Left VS 4-2-3-1 Right)
                // Left side: Home Team (Blue nodes)
                val blueTeam = Color(0xFF1E88E5)
                // GK
                drawCircle(blueTeam, 6.dp.toPx(), Offset(25.dp.toPx(), h / 2))
                // Defenders
                drawCircle(blueTeam, 6.dp.toPx(), Offset(50.dp.toPx(), h * 0.2f))
                drawCircle(blueTeam, 6.dp.toPx(), Offset(50.dp.toPx(), h * 0.4f))
                drawCircle(blueTeam, 6.dp.toPx(), Offset(50.dp.toPx(), h * 0.6f))
                drawCircle(blueTeam, 6.dp.toPx(), Offset(50.dp.toPx(), h * 0.8f))
                // Midfielders
                drawCircle(blueTeam, 6.dp.toPx(), Offset(90.dp.toPx(), h * 0.3f))
                drawCircle(blueTeam, 6.dp.toPx(), Offset(80.dp.toPx(), h * 0.5f))
                drawCircle(blueTeam, 6.dp.toPx(), Offset(90.dp.toPx(), h * 0.7f))
                // Attackers
                drawCircle(blueTeam, 6.dp.toPx(), Offset(130.dp.toPx(), h * 0.25f))
                drawCircle(blueTeam, 6.dp.toPx(), Offset(140.dp.toPx(), h * 0.5f)) // CF
                drawCircle(blueTeam, 6.dp.toPx(), Offset(130.dp.toPx(), h * 0.75f))

                // Right side: Away Team (Red/Purple nodes)
                val orangeTeam = Color(0xFFF4511E)
                // GK
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 25.dp.toPx(), h / 2))
                // Defenders
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 50.dp.toPx(), h * 0.2f))
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 55.dp.toPx(), h * 0.4f))
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 55.dp.toPx(), h * 0.6f))
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 50.dp.toPx(), h * 0.8f))
                // Defensive Midfielders (2)
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 90.dp.toPx(), h * 0.35f))
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 90.dp.toPx(), h * 0.65f))
                // Attacking Midfielders (3)
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 125.dp.toPx(), h * 0.25f))
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 120.dp.toPx(), h * 0.5f)) // CAM
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 125.dp.toPx(), h * 0.75f))
                // Striker (1)
                drawCircle(orangeTeam, 6.dp.toPx(), Offset(w - 150.dp.toPx(), h * 0.5f))

                // Draw tactical arrows of pressing or runs
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(w - 120.dp.toPx(), h * 0.5f),
                    end = Offset(w - 138.dp.toPx(), h * 0.5f),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(140.dp.toPx(), h * 0.5f),
                    end = Offset(165.dp.toPx(), h * 0.5f),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Position indicators legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF1E88E5), CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Home Position Nodes", fontSize = 10.sp, color = MutedGrey)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFF4511E), CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Away Position Nodes", fontSize = 10.sp, color = MutedGrey)
            }
        }
    }
}
