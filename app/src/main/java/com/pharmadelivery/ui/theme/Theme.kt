package com.pharmadelivery.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PharmaGreen,
    onPrimary = Color.White,
    primaryContainer = PharmaGreenContainer,
    onPrimaryContainer = PharmaGreenDark,
    secondary = PharmaTeal,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = StatusCancelled,
    outline = DividerColor
)

private val DarkColorScheme = darkColorScheme(
    primary = PharmaGreenDarkMode,
    onPrimary = Color.Black,
    primaryContainer = PharmaGreenDark,
    onPrimaryContainer = PharmaGreenLight,
    secondary = PharmaTealLight,
    onSecondary = Color.Black,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = Color(0xFFB0B0B0),
    error = StatusCancelled,
    outline = Color(0xFF424242)
)

@Composable
fun PharmaDeliveryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PharmaTypography,
        content = content
    )
}

// Role-specific color variants for their respective portals
@Composable
fun CustomerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(primary = CustomerAccent),
        typography = PharmaTypography,
        content = content
    )
}

@Composable
fun PharmacyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(primary = PharmacyAccent),
        typography = PharmaTypography,
        content = content
    )
}

@Composable
fun RiderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(primary = RiderAccent),
        typography = PharmaTypography,
        content = content
    )
}

@Composable
fun AdminTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(primary = AdminAccent),
        typography = PharmaTypography,
        content = content
    )
}
