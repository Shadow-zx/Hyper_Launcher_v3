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
    var themeColor by remember {
        mutableStateOf(if (isInPreview) 0xFF3F51B5.toInt() else LauncherPreferences.PREF_THEME_COLOR)
    }

    if (!isInPreview) {
        DisposableEffect(Unit) {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                when (key) {
                    "app_theme" -> themePref = LauncherPreferences.DEFAULT_PREF.getString("app_theme", "system") ?: "system"
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

    val primaryColor = if (themePref == "custom") Color(themeColor) else colorResource(R.color.minebutton_color)

    val colorScheme = if (themePref == "custom") {
        generateCustomColorScheme(primaryColor, isDark)
    } else {
        if (isDark) {
            darkColorScheme(
                primary = primaryColor,
                onPrimary = colorResource(R.color.minebutton_text_color),
                background = colorResource(R.color.background_app),
                surface = colorResource(R.color.background_status_bar),
                onSurface = colorResource(R.color.primary_text),
                surfaceVariant = colorResource(R.color.background_overlay),
                onSurfaceVariant = colorResource(R.color.secondary_text),
                outline = colorResource(R.color.divider),
                error = colorResource(R.color.warning),
            )
        } else {
            lightColorScheme(
                primary = primaryColor,
                onPrimary = colorResource(R.color.minebutton_text_color),
                background = colorResource(R.color.background_app),
                surface = colorResource(R.color.background_status_bar),
                onSurface = colorResource(R.color.primary_text),
                surfaceVariant = colorResource(R.color.background_overlay),
                onSurfaceVariant = colorResource(R.color.secondary_text),
                outline = colorResource(R.color.divider),
                error = colorResource(R.color.warning),
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

        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            background = tintedBackground,
            surface = tintedSurface,
            onSurface = Color.White.copy(alpha = 0.9f),
            surfaceVariant = primary.copy(alpha = 0.16f).compositeOver(darkBackground),
            onSurfaceVariant = Color.White.copy(alpha = 0.7f),
            outline = primary.copy(alpha = 0.5f),
            error = Color(0xFFCF6679)
        )
    } else {

        val lightBackground = Color(0xFFF2F2F2)
        val tintedBackground = primary.copy(alpha = 0.05f).compositeOver(lightBackground)
        val tintedSurface = primary.copy(alpha = 0.08f).compositeOver(Color.White)

        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            background = tintedBackground,
            surface = tintedSurface,
            onSurface = Color.Black.copy(alpha = 0.9f),
            surfaceVariant = primary.copy(alpha = 0.12f).compositeOver(lightBackground),
            onSurfaceVariant = Color.Black.copy(alpha = 0.7f),
            outline = primary.copy(alpha = 0.4f),
            error = Color(0xFFB00020)
        )
    }
}
