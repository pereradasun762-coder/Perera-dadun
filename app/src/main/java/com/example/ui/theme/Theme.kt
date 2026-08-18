package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KattiyaDarkColorScheme = darkColorScheme(
    primary = PrimaryViolet,
    onPrimary = Color.White,
    primaryContainer = PrimaryVioletDark,
    onPrimaryContainer = PrimaryVioletLight,
    secondary = SecondaryCyan,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryCyanDark,
    onSecondaryContainer = SecondaryCyanLight,
    tertiary = AccentAmber,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = AccentAmberLight,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = DarkSurfaceElevated,
    outline = DarkSurfaceBorder,
    outlineVariant = Color(0xFF3B3666),
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KattiyaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
