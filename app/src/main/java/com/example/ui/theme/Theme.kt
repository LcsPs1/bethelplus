package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val BethelDarkColorScheme = darkColorScheme(
    primary = BethelGoldLight,
    onPrimary = Color(0xFF3F2E05),
    primaryContainer = Color(0xFF584109),
    onPrimaryContainer = BethelGoldContainer,
    secondary = BethelOliveContainer,
    onSecondary = Color(0xFF1E3524),
    secondaryContainer = BethelOlive,
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = BethelTerracottaLight,
    onTertiary = Color(0xFF4A1F13),
    background = BethelMidnightBackground,
    onBackground = BethelMidnightTextPrimary,
    surface = BethelMidnightSurface,
    onSurface = BethelMidnightTextPrimary,
    surfaceVariant = BethelMidnightSurfaceVariant,
    onSurfaceVariant = BethelMidnightTextSecondary,
    outline = BethelMidnightBorder,
    outlineVariant = Color(0xFF23324C)
)

private val BethelLightColorScheme = lightColorScheme(
    primary = BethelGoldDark,
    onPrimary = Color.White,
    primaryContainer = BethelGoldContainer,
    onPrimaryContainer = BethelOnGoldContainer,
    secondary = BethelOlive,
    onSecondary = Color.White,
    secondaryContainer = BethelOliveContainer,
    onSecondaryContainer = Color(0xFF1B3322),
    tertiary = BethelTerracotta,
    onTertiary = Color.White,
    background = BethelSandBackground,
    onBackground = BethelSandTextPrimary,
    surface = BethelSandSurface,
    onSurface = BethelSandTextPrimary,
    surfaceVariant = BethelSandSurfaceVariant,
    onSurfaceVariant = BethelSandTextSecondary,
    outline = BethelSandBorder,
    outlineVariant = Color(0xFFD6C8B7)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default false to preserve Bethel+ sacred aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> BethelDarkColorScheme
        else -> BethelLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
