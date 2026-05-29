package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HighDensityColorScheme = lightColorScheme(
    primary = PitchGreen,
    secondary = FieldEmerald,
    tertiary = NeonLime,
    background = StadiumBlack,
    surface = CardSurface,
    onPrimary = Color.White,
    onSecondary = LightGrey,
    onTertiary = LightGrey,
    onBackground = LightGrey,
    onSurface = LightGrey,
    surfaceVariant = FieldEmerald,
    onSurfaceVariant = MutedGrey,
    outline = PitchDarkLine,
    error = LiveRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // We transition to high-density polished Light theme by default
    dynamicColor: Boolean = false, // Use our configured gorgeous High Density palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HighDensityColorScheme,
        typography = Typography,
        content = content
    )
}
