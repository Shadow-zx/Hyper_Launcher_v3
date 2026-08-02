package net.kdt.pojavlaunch.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.screens.settings.layouts.CardPosition
import net.kdt.pojavlaunch.screens.settings.preferences.SettingsActionItem
import net.kdt.pojavlaunch.screens.settings.layouts.SettingsCard
import net.kdt.pojavlaunch.screens.settings.layouts.SettingsScreenWrapper
import net.kdt.pojavlaunch.screens.settings.preferences.SingleChoiceDialog

@Composable
fun AppearanceSettingsScreen(
    onBack: () -> Unit
) {
    var screenTransition by remember { mutableStateOf(LauncherPreferences.PREF_SCREEN_TRANSITION) }
    var appTheme by remember { mutableStateOf(LauncherPreferences.PREF_THEME) }
    val context = LocalContext.current
    var showTransitionDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val transitionOptions = listOf("none", "fade", "bounce")
    val transitionOptionNames = transitionOptions.map { id ->
        when (id) {
            "none" -> stringResource(R.string.preference_screen_transition_none)
            "fade" -> stringResource(R.string.preference_screen_transition_fade)
            "bounce" -> stringResource(R.string.preference_screen_transition_bounce)
            else -> id
        }
    }

    val themeOptions = listOf("system", "light", "dark")
    val themeOptionNames = themeOptions.map { id ->
        when (id) {
            "system" -> stringResource(R.string.preference_app_theme_system)
            "light" -> stringResource(R.string.preference_app_theme_light)
            "dark" -> stringResource(R.string.preference_app_theme_dark)
            else -> id
        }
    }

    SettingsScreenWrapper(
        title = stringResource(R.string.preference_appearance_title),
        onBack = onBack,
        addTopGap = true
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SettingsCard(position = CardPosition.TOP, useSurface = true) {
                SettingsActionItem(
                    title = stringResource(R.string.preference_app_theme_title),
                    summary = themeOptionNames[themeOptions.indexOf(appTheme).coerceAtLeast(0)],
                    icon = Icons.Default.Palette,
                    onClick = { showThemeDialog = true }
                )
            }

            SettingsCard(position = CardPosition.BOTTOM, useSurface = true) {
                SettingsActionItem(
                    title = stringResource(R.string.preference_screen_transition_title),
                    summary = transitionOptionNames[transitionOptions.indexOf(screenTransition).coerceAtLeast(0)],
                    icon = Icons.Default.Animation,
                    onClick = { showTransitionDialog = true }
                )
            }
        }
        
    }

    if (showThemeDialog) {
        SingleChoiceDialog(
            title = stringResource(R.string.preference_app_theme_title),
            options = themeOptionNames,
            optionValues = themeOptions,
            selectedValue = appTheme,
            onValueChange = { newValue ->
                appTheme = newValue
                LauncherPreferences.DEFAULT_PREF.edit { putString("app_theme", newValue) }
                LauncherPreferences.loadPreferences(context)
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showTransitionDialog) {
        SingleChoiceDialog(
            title = stringResource(R.string.preference_screen_transition_title),
            options = transitionOptionNames,
            optionValues = transitionOptions,
            selectedValue = screenTransition,
            onValueChange = { newValue ->
                screenTransition = newValue
                LauncherPreferences.DEFAULT_PREF.edit { putString("screen_transition", newValue) }
                LauncherPreferences.loadPreferences(context)
            },
            onDismiss = { showTransitionDialog = false }
        )
    }
}
