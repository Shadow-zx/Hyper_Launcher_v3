package net.kdt.pojavlaunch.screens.theme

import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.prefs.LauncherPreferences

@Composable
fun PojavTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    var themePref by remember { mutableStateOf(LauncherPreferences.PREF_THEME) }

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "app_theme") {
                themePref = LauncherPreferences.DEFAULT_PREF.getString("app_theme", "system") ?: "system"
            }
        }
        LauncherPreferences.DEFAULT_PREF.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            LauncherPreferences.DEFAULT_PREF.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val isDark = darkTheme ?: when (themePref) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = colorResource(R.color.minebutton_color),
            onPrimary = colorResource(R.color.minebutton_text_color),
            background = colorResource(R.color.background_app),
            surface = colorResource(R.color.background_status_bar),
            onSurface = colorResource(R.color.primary_text),
            surfaceVariant = colorResource(R.color.background_overlay),
            onSurfaceVariant = colorResource(R.color.secondary_text),
            outline = colorResource(R.color.divider),
        )
    } else {
        lightColorScheme(
            primary = colorResource(R.color.minebutton_color),
            onPrimary = colorResource(R.color.minebutton_text_color),
            background = colorResource(R.color.background_app),
            surface = colorResource(R.color.background_status_bar),
            onSurface = colorResource(R.color.primary_text),
            surfaceVariant = colorResource(R.color.background_overlay),
            onSurfaceVariant = colorResource(R.color.secondary_text),
            outline = colorResource(R.color.divider),
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
