package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.ui.viewmodel.ThemeMode
import com.example.ui.viewmodel.ThemePalette

fun getAppColorScheme(
    isDark: Boolean,
    isOled: Boolean,
    palette: ThemePalette
): ColorScheme {
    val bg = if (isOled) SurfaceOled else if (isDark) SurfaceDark else SurfaceLight
    val card = if (isOled) CardOled else if (isDark) CardDark else CardLight
    val textPrimary = if (isDark || isOled) TextPrimaryDark else TextPrimaryLight
    val textSecondary = if (isDark || isOled) TextSecondaryDark else TextSecondaryLight
    val outline = if (isOled) OutlineOled else if (isDark) OutlineDark else OutlineLight

    return when (palette) {
        ThemePalette.INDIGO -> {
            if (isDark || isOled) {
                darkColorScheme(
                    primary = IndigoLight,
                    onPrimary = Color.White,
                    primaryContainer = DarkIndigoContainer,
                    onPrimaryContainer = Color(0xFFC7D2FE),
                    secondary = CyanLight,
                    onSecondary = Color(0xFF00363A),
                    secondaryContainer = DarkCyanContainer,
                    onSecondaryContainer = Color(0xFFA5F3FC),
                    tertiary = AmberLight,
                    onTertiary = Color(0xFF451A03),
                    tertiaryContainer = DarkAmberContainer,
                    onTertiaryContainer = Color(0xFFFDE68A),
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = if (isOled) Color(0xFF1E1E1E) else Color(0xFF334155),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            } else {
                lightColorScheme(
                    primary = IndigoPrimary,
                    onPrimary = Color.White,
                    primaryContainer = IndigoContainer,
                    onPrimaryContainer = IndigoDark,
                    secondary = CyanSecondary,
                    onSecondary = Color.White,
                    secondaryContainer = CyanContainer,
                    onSecondaryContainer = CyanDark,
                    tertiary = AmberTertiary,
                    onTertiary = Color.White,
                    tertiaryContainer = AmberContainer,
                    onTertiaryContainer = Color(0xFF78350F),
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = Color(0xFFF1F5F9),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            }
        }

        ThemePalette.EMERALD -> {
            if (isDark || isOled) {
                darkColorScheme(
                    primary = EmeraldLight,
                    onPrimary = Color.White,
                    primaryContainer = DarkEmeraldContainer,
                    onPrimaryContainer = Color(0xFFA7F3D0),
                    secondary = TealLight,
                    onSecondary = Color(0xFF042F2E),
                    secondaryContainer = DarkTealContainer,
                    onSecondaryContainer = Color(0xFF99F6E4),
                    tertiary = AmberLight,
                    onTertiary = Color(0xFF451A03),
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = if (isOled) Color(0xFF1E1E1E) else Color(0xFF1E2E2A),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            } else {
                lightColorScheme(
                    primary = EmeraldPrimary,
                    onPrimary = Color.White,
                    primaryContainer = EmeraldContainer,
                    onPrimaryContainer = Color(0xFF064E3B),
                    secondary = TealSecondary,
                    onSecondary = Color.White,
                    secondaryContainer = TealContainer,
                    onSecondaryContainer = DarkTealContainer,
                    tertiary = AmberTertiary,
                    onTertiary = Color.White,
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = Color(0xFFF0FDF4),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            }
        }

        ThemePalette.SUNSET -> {
            if (isDark || isOled) {
                darkColorScheme(
                    primary = SunsetLight,
                    onPrimary = Color.White,
                    primaryContainer = DarkSunsetContainer,
                    onPrimaryContainer = Color(0xFFFFEDD5),
                    secondary = CrimsonLight,
                    onSecondary = Color.White,
                    secondaryContainer = DarkCrimsonContainer,
                    onSecondaryContainer = Color(0xFFFFE4E6),
                    tertiary = AmberLight,
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = if (isOled) Color(0xFF1E1E1E) else Color(0xFF2E201B),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            } else {
                lightColorScheme(
                    primary = SunsetPrimary,
                    onPrimary = Color.White,
                    primaryContainer = SunsetContainer,
                    onPrimaryContainer = Color(0xFF7C2D12),
                    secondary = CrimsonSecondary,
                    onSecondary = Color.White,
                    secondaryContainer = CrimsonContainer,
                    onSecondaryContainer = Color(0xFF9F1239),
                    tertiary = AmberTertiary,
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = Color(0xFFFFF7ED),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            }
        }

        ThemePalette.VIOLET -> {
            if (isDark || isOled) {
                darkColorScheme(
                    primary = VioletLight,
                    onPrimary = Color.White,
                    primaryContainer = DarkVioletContainer,
                    onPrimaryContainer = Color(0xFFDDD6FE),
                    secondary = PinkLight,
                    onSecondary = Color.White,
                    secondaryContainer = DarkPinkContainer,
                    onSecondaryContainer = Color(0xFFFCE7F3),
                    tertiary = CyanLight,
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = if (isOled) Color(0xFF1E1E1E) else Color(0xFF2D1B4E),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            } else {
                lightColorScheme(
                    primary = VioletPrimary,
                    onPrimary = Color.White,
                    primaryContainer = VioletContainer,
                    onPrimaryContainer = Color(0xFF4C1D95),
                    secondary = PinkSecondary,
                    onSecondary = Color.White,
                    secondaryContainer = PinkContainer,
                    onSecondaryContainer = Color(0xFF831843),
                    tertiary = CyanSecondary,
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = Color(0xFFF5F3FF),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            }
        }

        ThemePalette.OCEAN -> {
            if (isDark || isOled) {
                darkColorScheme(
                    primary = OceanLight,
                    onPrimary = Color.White,
                    primaryContainer = DarkOceanContainer,
                    onPrimaryContainer = Color(0xFFBAE6FD),
                    secondary = BlueLight,
                    onSecondary = Color.White,
                    secondaryContainer = DarkBlueContainer,
                    onSecondaryContainer = Color(0xFFBFDBFE),
                    tertiary = TealLight,
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = if (isOled) Color(0xFF1E1E1E) else Color(0xFF172554),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            } else {
                lightColorScheme(
                    primary = OceanPrimary,
                    onPrimary = Color.White,
                    primaryContainer = OceanContainer,
                    onPrimaryContainer = Color(0xFF0369A1),
                    secondary = BlueSecondary,
                    onSecondary = Color.White,
                    secondaryContainer = BlueContainer,
                    onSecondaryContainer = Color(0xFF1E40AF),
                    tertiary = TealSecondary,
                    background = bg,
                    onBackground = textPrimary,
                    surface = card,
                    onSurface = textPrimary,
                    surfaceVariant = Color(0xFFF0F9FF),
                    onSurfaceVariant = textSecondary,
                    outline = outline,
                    error = RoseError
                )
            }
        }
    }
}

@Composable
fun ClassmateAiTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    themePalette: ThemePalette = ThemePalette.INDIGO,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.OLED -> true
    }
    val isOled = themeMode == ThemeMode.OLED

    val colorScheme = getAppColorScheme(isDark = isDark, isOled = isOled, palette = themePalette)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

