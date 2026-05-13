package com.brindawan.mobile.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrindawanBlue = Color(0xFF0597F2)
private val BrindawanGold = Color(0xFFF2C029)
private val BrindawanAmber = Color(0xFFF2B544)
private val BrindawanSand = Color(0xFFF2D194)
private val BrindawanPink = Color(0xFFEB008B)

private val BrindawanLightColors: ColorScheme = lightColorScheme(
    primary = BrindawanBlue,
    onPrimary = Color.White,
    secondary = BrindawanGold,
    onSecondary = Color.Black,
    tertiary = BrindawanPink,
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1D1B20),
    surface = Color.White,
    onSurface = Color(0xFF1D1B20),
    surfaceVariant = BrindawanSand,
    onSurfaceVariant = Color(0xFF1D1B20),
    outline = BrindawanAmber,
)

private val BrindawanDarkColors: ColorScheme = darkColorScheme(
    primary = BrindawanBlue,
    onPrimary = Color.White,
    secondary = BrindawanGold,
    onSecondary = Color.Black,
    tertiary = BrindawanPink,
    onTertiary = Color.White,
    background = Color(0xFF111318),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF2F3137),
    onSurfaceVariant = BrindawanSand,
    outline = BrindawanAmber,
)

@Composable
fun BrindawanTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) BrindawanDarkColors else BrindawanLightColors,
        content = content,
    )
}

