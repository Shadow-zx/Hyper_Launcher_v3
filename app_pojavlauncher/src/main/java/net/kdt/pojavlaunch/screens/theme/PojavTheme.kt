package net.kdt.pojavlaunch.screens.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PojavColors = darkColorScheme(
    primary = Color(0xFFFFFFFF), // minebutton_color
    onPrimary = Color(0xFF000000), // minebutton_text_color
    background = Color(0xFF0D0D0D), // background_app
    surface = Color(0xFF121212), // background_status_bar
    onSurface = Color(0xFFFFFFFF), // primary_text
    surfaceVariant = Color(0xFF202020), // background_overlay
    onSurfaceVariant = Color(0xFF8A8A8A), // secondary_text
    outline = Color(0xFF202020), // divider
)

@Composable
fun PojavTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = PojavColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
