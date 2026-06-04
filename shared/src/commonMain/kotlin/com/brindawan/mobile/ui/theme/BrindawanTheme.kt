package com.brindawan.mobile.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// ─── Shared brand tokens ────────────────────────────────────────────────────
private val BrandSky     = Color(0xFF0597F2)
private val BrandGold    = Color(0xFFF2C029)
private val BrandMagenta = Color(0xFFEB008B)
private val BrandSand    = Color(0xFFF2D194)
private val BrandBronze  = Color(0xFFD08C3D)

// ─── Website-exact palette (AGENTS.md) ──────────────────────────────────────
private val TeaGreen    = Color(0xFFCCD5AE)   // #ccd5ae
private val Beige       = Color(0xFFE9EDC9)   // #e9edc9
private val Cornsilk    = Color(0xFFFEFAE0)   // #fefae0
private val PapayaWhip  = Color(0xFFFAEDCD)   // #faedcd
private val LightBronze = Color(0xFFD4A373)   // #d4a373

// ─── Soft variant ───────────────────────────────────────────────────────────
// Muted, pastel tones — gentle on the eye
private val SoftLightColors: ColorScheme = lightColorScheme(
    primary             = Color(0xFF6AADCF),          // desaturated sky
    onPrimary           = Color.White,
    secondary           = Color(0xFFD4BB72),          // muted gold
    onSecondary         = Color(0xFF3B2500),
    tertiary            = Color(0xFFCF80A8),          // blush magenta
    onTertiary          = Color.White,
    primaryContainer    = Color(0xFFDCF0FF),
    onPrimaryContainer  = Color(0xFF003A5A),
    secondaryContainer  = Color(0xFFFFF0CA),
    onSecondaryContainer= Color(0xFF4A3300),
    tertiaryContainer   = Color(0xFFFAD8EC),
    onTertiaryContainer = Color(0xFF5A0034),
    background          = Color(0xFFFFFDF8),
    onBackground        = Color(0xFF2A241C),
    surface             = Color(0xFFFFFFFF),
    onSurface           = Color(0xFF2A241C),
    surfaceTint         = Color(0xFF6AADCF),
    surfaceVariant      = Color(0xFFF3EDE0),
    onSurfaceVariant    = Color(0xFF6B6259),
    outline             = BrandBronze.copy(alpha = 0.45f),
)

private val SoftDarkColors: ColorScheme = darkColorScheme(
    primary             = Color(0xFFADD8F0),
    onPrimary           = Color(0xFF003A5A),
    secondary           = Color(0xFFEDD08A),
    onSecondary         = Color(0xFF3B2500),
    tertiary            = Color(0xFFEBA8CC),
    onTertiary          = Color(0xFF4A002C),
    primaryContainer    = Color(0xFF1A4E6A),
    onPrimaryContainer  = Color(0xFFDCF0FF),
    secondaryContainer  = Color(0xFF4A3900),
    onSecondaryContainer= Color(0xFFFFF0CA),
    tertiaryContainer   = Color(0xFF5E1F45),
    onTertiaryContainer = Color(0xFFFAD8EC),
    background          = Color(0xFF181512),
    onBackground        = Color(0xFFF0EBE0),
    surface             = Color(0xFF221E1A),
    onSurface           = Color(0xFFF0EBE0),
    surfaceTint         = Color(0xFFADD8F0),
    surfaceVariant      = Color(0xFF2E2922),
    onSurfaceVariant    = Color(0xFFCFC3B0),
    outline             = BrandSand.copy(alpha = 0.40f),
)

// ─── Bold variant ───────────────────────────────────────────────────────────
// Full-saturation brand colours, high contrast
private val BoldLightColors: ColorScheme = lightColorScheme(
    primary             = BrandSky,
    onPrimary           = Color.White,
    secondary           = BrandGold,
    onSecondary         = Color(0xFF3B2500),
    tertiary            = BrandMagenta,
    onTertiary          = Color.White,
    primaryContainer    = Color(0xFFB3DFFF),
    onPrimaryContainer  = Color(0xFF002D47),
    secondaryContainer  = Color(0xFFFFE082),
    onSecondaryContainer= Color(0xFF3B2500),
    tertiaryContainer   = Color(0xFFF48CB8),
    onTertiaryContainer = Color(0xFF4A0027),
    background          = Color(0xFFFFFBF0),
    onBackground        = Color(0xFF100E09),
    surface             = Color(0xFFFFFFFF),
    onSurface           = Color(0xFF100E09),
    surfaceTint         = BrandSky,
    surfaceVariant      = Color(0xFFEFE5CE),
    onSurfaceVariant    = Color(0xFF4A4030),
    outline             = BrandBronze,
)

private val BoldDarkColors: ColorScheme = darkColorScheme(
    primary             = Color(0xFF5ECEFF),
    onPrimary           = Color(0xFF00243B),
    secondary           = Color(0xFFFFD740),
    onSecondary         = Color(0xFF3B2500),
    tertiary            = Color(0xFFFF61B9),
    onTertiary          = Color(0xFF3A0020),
    primaryContainer    = Color(0xFF004D72),
    onPrimaryContainer  = Color(0xFFB3DFFF),
    secondaryContainer  = Color(0xFF614F00),
    onSecondaryContainer= Color(0xFFFFE082),
    tertiaryContainer   = Color(0xFF750040),
    onTertiaryContainer = Color(0xFFF48CB8),
    background          = Color(0xFF0E0C0A),
    onBackground        = Color(0xFFFAF5EA),
    surface             = Color(0xFF191612),
    onSurface           = Color(0xFFFAF5EA),
    surfaceTint         = Color(0xFF5ECEFF),
    surfaceVariant      = Color(0xFF27221A),
    onSurfaceVariant    = Color(0xFFDDD0BC),
    outline             = BrandBronze.copy(alpha = 0.75f),
)

// ─── Website-exact variant ──────────────────────────────────────────────────
// Derived entirely from the AGENTS.md brand palette:
// Tea Green #ccd5ae | Beige #e9edc9 | Cornsilk #fefae0
// Papaya Whip #faedcd | Light Bronze #d4a373
private val WebsiteExactLightColors: ColorScheme = lightColorScheme(
    primary             = LightBronze,
    onPrimary           = Color(0xFF3B1F00),
    secondary           = TeaGreen,
    onSecondary         = Color(0xFF1C2200),
    tertiary            = Color(0xFF8B6845),          // darkened bronze accent
    onTertiary          = Color.White,
    primaryContainer    = PapayaWhip,
    onPrimaryContainer  = Color(0xFF3B1F00),
    secondaryContainer  = Beige,
    onSecondaryContainer= Color(0xFF1C2200),
    tertiaryContainer   = TeaGreen.copy(alpha = 0.50f),
    onTertiaryContainer = Color(0xFF1C2200),
    background          = Cornsilk,
    onBackground        = Color(0xFF2C2010),
    surface             = Color(0xFFFFFFFF),
    onSurface           = Color(0xFF2C2010),
    surfaceTint         = LightBronze,
    surfaceVariant      = PapayaWhip,
    onSurfaceVariant    = Color(0xFF5C4A30),
    outline             = LightBronze.copy(alpha = 0.60f),
)

private val WebsiteExactDarkColors: ColorScheme = darkColorScheme(
    primary             = LightBronze,
    onPrimary           = Color(0xFF3B1F00),
    secondary           = TeaGreen,
    onSecondary         = Color(0xFF1C2200),
    tertiary            = Color(0xFFE8C49A),          // lightened bronze
    onTertiary          = Color(0xFF3B1F00),
    primaryContainer    = Color(0xFF5A3A18),
    onPrimaryContainer  = PapayaWhip,
    secondaryContainer  = Color(0xFF3A4520),
    onSecondaryContainer= Beige,
    tertiaryContainer   = Color(0xFF4A3010),
    onTertiaryContainer = Cornsilk,
    background          = Color(0xFF1A160E),
    onBackground        = Cornsilk,
    surface             = Color(0xFF231D13),
    onSurface           = Cornsilk,
    surfaceTint         = LightBronze,
    surfaceVariant      = Color(0xFF302818),
    onSurfaceVariant    = PapayaWhip,
    outline             = LightBronze.copy(alpha = 0.55f),
)

// ─── Theme variant selector ──────────────────────────────────────────────────
enum class BrindawanThemeVariant {
    /** Muted, pastel tones */
    Soft,
    /** Full-saturation brand colours */
    Bold,
    /** Exact palette from the design spec (tea-green / bronze / cornsilk) */
    WebsiteExact,
}

// ─── Public theme composable ─────────────────────────────────────────────────
@Composable
fun BrindawanTheme(
    darkTheme: Boolean,
    variant: BrindawanThemeVariant = BrindawanThemeVariant.Bold,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (variant) {
        BrindawanThemeVariant.Soft         -> if (darkTheme) SoftDarkColors         else SoftLightColors
        BrindawanThemeVariant.Bold         -> if (darkTheme) BoldDarkColors         else BoldLightColors
        BrindawanThemeVariant.WebsiteExact -> if (darkTheme) WebsiteExactDarkColors else WebsiteExactLightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

@Composable
fun brindawanBottomNavContainerColor(): Color {
    return if (MaterialTheme.colorScheme.background.luminance() > 0.5f) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.86f)
    }
}
