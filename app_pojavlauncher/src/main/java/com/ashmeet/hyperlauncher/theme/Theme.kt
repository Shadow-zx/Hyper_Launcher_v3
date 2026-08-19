package com.ashmeet.hyperlauncher.theme

import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import net.ashmeet.hyperlauncher.R
import com.ashmeet.hyperlauncher.prefs.LauncherPreferences

@Composable
fun PojavTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val isInPreview = LocalInspectionMode.current
    var themePref by remember {
        mutableStateOf(if (isInPreview) "system" else LauncherPreferences.PREF_THEME)
    }
    var isCustomTheme by remember {
        mutableStateOf(if (isInPreview) false else LauncherPreferences.PREF_CUSTOM_THEME)
    }
    var themeColor by remember {
        mutableStateOf(if (isInPreview) 0xFF3F51B5.toInt() else LauncherPreferences.PREF_THEME_COLOR)
    }

    if (!isInPreview) {
        DisposableEffect(Unit) {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                when (key) {
                    "app_theme" -> themePref = LauncherPreferences.DEFAULT_PREF.getString("app_theme", "system") ?: "system"
                    "app_custom_theme" -> isCustomTheme = LauncherPreferences.DEFAULT_PREF.getBoolean("app_custom_theme", false)
                    "app_theme_color" -> themeColor = LauncherPreferences.DEFAULT_PREF.getInt("app_theme_color", 0xFF3F51B5.toInt())
                }
            }
            LauncherPreferences.DEFAULT_PREF.registerOnSharedPreferenceChangeListener(listener)
            onDispose {
                LauncherPreferences.DEFAULT_PREF.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    val isDark = darkTheme ?: when (themePref) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val primaryColor = if (isCustomTheme) Color(themeColor) else colorResource(R.color.minebutton_color)

    val colorScheme = if (isCustomTheme) {
        generateCustomColorScheme(primaryColor, isDark)
    } else {
        if (isDark) {
            darkColorScheme(
                primary = primaryColor,
                onPrimary = colorResource(R.color.minebutton_text_color),
                primaryContainer = primaryColor.copy(alpha = 0.3f),
                onPrimaryContainer = Color.White,
                secondary = primaryColor,
                onSecondary = colorResource(R.color.minebutton_text_color),
                secondaryContainer = primaryColor.copy(alpha = 0.2f),
                onSecondaryContainer = Color.White,
                tertiary = primaryColor,
                onTertiary = colorResource(R.color.minebutton_text_color),
                tertiaryContainer = primaryColor.copy(alpha = 0.15f),
                onTertiaryContainer = Color.White,
                error = colorResource(R.color.warning),
                onError = Color.Black,
                errorContainer = Color(0xFF93000A),
                onErrorContainer = Color(0xFFFFDAD6),
                background = colorResource(R.color.background_app),
                onBackground = colorResource(R.color.primary_text),
                surface = colorResource(R.color.background_status_bar),
                onSurface = colorResource(R.color.primary_text),
                surfaceVariant = colorResource(R.color.background_overlay),
                onSurfaceVariant = colorResource(R.color.secondary_text),
                outline = colorResource(R.color.divider),
                outlineVariant = colorResource(R.color.divider).copy(alpha = 0.5f),
                scrim = Color.Black,
                inverseSurface = Color.White,
                inverseOnSurface = Color.Black,
                inversePrimary = primaryColor,
                surfaceDim = Color(0xFF1A1A1A),
                surfaceBright = Color(0xFF3B3B3B),
                surfaceContainerLowest = Color(0xFF0F0F0F),
                surfaceContainerLow = Color(0xFF1A1A1A),
                surfaceContainer = Color(0xFF212121),
                surfaceContainerHigh = Color(0xFF2B2B2B),
                surfaceContainerHighest = Color(0xFF333333)
            )
        } else {
            lightColorScheme(
                primary = primaryColor,
                onPrimary = colorResource(R.color.minebutton_text_color),
                primaryContainer = primaryColor.copy(alpha = 0.1f),
                onPrimaryContainer = Color.Black,
                secondary = primaryColor,
                onSecondary = colorResource(R.color.minebutton_text_color),
                secondaryContainer = primaryColor.copy(alpha = 0.05f),
                onSecondaryContainer = Color.Black,
                tertiary = primaryColor,
                onTertiary = colorResource(R.color.minebutton_text_color),
                tertiaryContainer = primaryColor.copy(alpha = 0.03f),
                onTertiaryContainer = Color.Black,
                error = colorResource(R.color.warning),
                onError = Color.White,
                errorContainer = Color(0xFFFFDAD6),
                onErrorContainer = Color(0xFF410002),
                background = colorResource(R.color.background_app),
                onBackground = colorResource(R.color.primary_text),
                surface = colorResource(R.color.background_status_bar),
                onSurface = colorResource(R.color.primary_text),
                surfaceVariant = colorResource(R.color.background_overlay),
                onSurfaceVariant = colorResource(R.color.secondary_text),
                outline = colorResource(R.color.divider),
                outlineVariant = colorResource(R.color.divider).copy(alpha = 0.5f),
                scrim = Color.Black,
                inverseSurface = Color(0xFF313033),
                inverseOnSurface = Color(0xFFF4EFF4),
                inversePrimary = primaryColor,
                surfaceDim = Color(0xFFDED8E1),
                surfaceBright = Color(0xFFFEF7FF),
                surfaceContainerLowest = Color.White,
                surfaceContainerLow = Color(0xFFF7F2FA),
                surfaceContainer = Color(0xFFF3EDF7),
                surfaceContainerHigh = Color(0xFFECE6F0),
                surfaceContainerHighest = Color(0xFFE6E0E9)
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

private fun generateCustomColorScheme(primary: Color, isDark: Boolean): ColorScheme {
    val onPrimary = if (primary.luminance() > 0.5f) Color.Black else Color.White

    return if (isDark) {
        val darkBackground = Color(0xFF121212)
        val tintedBackground = primary.copy(alpha = 0.08f).compositeOver(darkBackground)
        val tintedSurface = primary.copy(alpha = 0.12f).compositeOver(darkBackground)
        val onSurface = Color.White.copy(alpha = 0.9f)

        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primary.copy(alpha = 0.3f).compositeOver(darkBackground),
            onPrimaryContainer = Color.White,
            secondary = primary,
            onSecondary = onPrimary,
            secondaryContainer = primary.copy(alpha = 0.2f).compositeOver(darkBackground),
            onSecondaryContainer = Color.White,
            tertiary = primary,
            onTertiary = onPrimary,
            tertiaryContainer = primary.copy(alpha = 0.15f).compositeOver(darkBackground),
            onTertiaryContainer = Color.White,
            error = Color(0xFFCF6679),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = tintedBackground,
            onBackground = onSurface,
            surface = tintedSurface,
            onSurface = onSurface,
            surfaceVariant = primary.copy(alpha = 0.16f).compositeOver(darkBackground),
            onSurfaceVariant = Color.White.copy(alpha = 0.7f),
            outline = primary.copy(alpha = 0.5f),
            outlineVariant = primary.copy(alpha = 0.2f),
            scrim = Color.Black,
            inverseSurface = Color.White,
            inverseOnSurface = Color.Black,
            inversePrimary = primary,
            surfaceDim = primary.copy(alpha = 0.1f).compositeOver(darkBackground),
            surfaceBright = primary.copy(alpha = 0.2f).compositeOver(darkBackground),
            surfaceContainerLowest = Color(0xFF0F0F0F),
            surfaceContainerLow = Color(0xFF1A1A1A),
            surfaceContainer = Color(0xFF212121),
            surfaceContainerHigh = Color(0xFF2B2B2B),
            surfaceContainerHighest = Color(0xFF333333)
        )
    } else {
        val lightBackground = Color(0xFFF2F2F2)
        val tintedBackground = primary.copy(alpha = 0.05f).compositeOver(lightBackground)
        val tintedSurface = primary.copy(alpha = 0.08f).compositeOver(Color.White)
        val onSurface = Color.Black.copy(alpha = 0.9f)

        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primary.copy(alpha = 0.15f).compositeOver(Color.White),
            onPrimaryContainer = Color.Black,
            secondary = primary,
            onSecondary = onPrimary,
            secondaryContainer = primary.copy(alpha = 0.1f).compositeOver(Color.White),
            onSecondaryContainer = Color.Black,
            tertiary = primary,
            onTertiary = onPrimary,
            tertiaryContainer = primary.copy(alpha = 0.07f).compositeOver(Color.White),
            onTertiaryContainer = Color.Black,
            error = Color(0xFFB00020),
            onError = Color.White,
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = tintedBackground,
            onBackground = onSurface,
            surface = tintedSurface,
            onSurface = onSurface,
            surfaceVariant = primary.copy(alpha = 0.12f).compositeOver(lightBackground),
            onSurfaceVariant = Color.Black.copy(alpha = 0.7f),
            outline = primary.copy(alpha = 0.4f),
            outlineVariant = primary.copy(alpha = 0.15f),
            scrim = Color.Black,
            inverseSurface = Color(0xFF313033),
            inverseOnSurface = Color(0xFFF4EFF4),
            inversePrimary = primary,
            surfaceDim = Color(0xFFDED8E1),
            surfaceBright = Color(0xFFFEF7FF),
            surfaceContainerLowest = Color.White,
            surfaceContainerLow = Color(0xFFF7F2FA),
            surfaceContainer = Color(0xFFF3EDF7),
            surfaceContainerHigh = Color(0xFFECE6F0),
            surfaceContainerHighest = Color(0xFFE6E0E9)
        )
    }
}
