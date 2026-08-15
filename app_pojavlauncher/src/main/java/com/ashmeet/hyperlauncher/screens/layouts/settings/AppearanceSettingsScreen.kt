package com.ashmeet.hyperlauncher.screens.layouts.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewSidebar
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.ashmeet.hyperlauncher.screens.layouts.settings.layouts.CardPosition
import com.ashmeet.hyperlauncher.screens.layouts.settings.layouts.SettingsCard
import com.ashmeet.hyperlauncher.screens.layouts.settings.layouts.SettingsScreenWrapper
import com.ashmeet.hyperlauncher.screens.layouts.settings.preferences.PreferenceCategory
import com.ashmeet.hyperlauncher.screens.layouts.settings.preferences.SettingsActionItem
import com.ashmeet.hyperlauncher.screens.layouts.settings.preferences.SettingsSliderItem
import com.ashmeet.hyperlauncher.screens.layouts.settings.preferences.SettingsSwitchItem
import com.ashmeet.hyperlauncher.screens.layouts.settings.preferences.SingleChoiceDialog
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.Tools
import com.ashmeet.hyperlauncher.prefs.LauncherPreferences
import java.io.File
import java.io.FileOutputStream

@Composable
fun AppearanceSettingsScreen(
    onBack: () -> Unit
) {
    var screenTransition by remember { mutableStateOf(LauncherPreferences.PREF_SCREEN_TRANSITION) }
    var appTheme by remember { mutableStateOf(LauncherPreferences.PREF_THEME) }
    var hideSidebar by remember { mutableStateOf(LauncherPreferences.PREF_HIDE_SIDEBAR) }
    
    var drawerSizePerc by remember { mutableFloatStateOf(LauncherPreferences.PREF_DRAWER_PULL_SIZE_PERC) }
    
    var drawerBgOpacity by remember { mutableFloatStateOf(LauncherPreferences.PREF_DRAWER_PULL_BG_OPACITY.toFloat()) }
    var drawerIconOpacity by remember { mutableFloatStateOf(LauncherPreferences.PREF_DRAWER_PULL_ICON_OPACITY.toFloat()) }
    var drawerHoldToMove by remember { mutableStateOf(LauncherPreferences.PREF_DRAWER_PULL_HOLD_TO_MOVE) }
    var drawerBackground by remember { mutableStateOf(LauncherPreferences.PREF_DRAWER_PULL_BACKGROUND) }
    var drawerIconPath by remember { mutableStateOf(LauncherPreferences.PREF_DRAWER_PULL_ICON_PATH) }
    
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

            PreferenceCategory(title = "Main Menu")
            SettingsCard(position = CardPosition.SINGLE, useSurface = true) {
                SettingsSwitchItem(
                    title = "Hide sidebar",
                    summary = "Hide action buttons on the left side of the main menu",
                    icon = Icons.AutoMirrored.Rounded.ViewSidebar,
                    checked = hideSidebar,
                    onCheckedChange = {
                        hideSidebar = it
                        LauncherPreferences.DEFAULT_PREF.edit { putBoolean("hide_sidebar", it) }
                        LauncherPreferences.PREF_HIDE_SIDEBAR = it
                    }
                )
            }

            PreferenceCategory(title = "Drawer Button")
            SettingsCard(position = CardPosition.TOP, useSurface = true) {
                SettingsSliderItem(
                    title = "Size",
                    icon = Icons.Rounded.AspectRatio,
                    value = drawerSizePerc,
                    valueRange = 10f..100f,
                    onValueChange = {
                        drawerSizePerc = it
                        LauncherPreferences.DEFAULT_PREF.edit { putFloat("drawer_pull_size_perc", it) }
                        LauncherPreferences.PREF_DRAWER_PULL_SIZE_PERC = it
                    },
                    valueSuffix = "%"
                )
            }
            SettingsCard(position = CardPosition.MIDDLE, useSurface = true) {
                SettingsSliderItem(
                    title = "Background Opacity",
                    icon = Icons.Rounded.Opacity,
                    value = drawerBgOpacity,
                    valueRange = 0f..100f,
                    onValueChange = {
                        drawerBgOpacity = it
                        LauncherPreferences.DEFAULT_PREF.edit { putInt("drawer_pull_opacity", it.toInt()) }
                        LauncherPreferences.PREF_DRAWER_PULL_BG_OPACITY = it.toInt()
                    },
                    valueSuffix = "%"
                )
            }
            SettingsCard(position = CardPosition.MIDDLE, useSurface = true) {
                SettingsSliderItem(
                    title = "Icon Opacity",
                    icon = Icons.Rounded.Opacity,
                    value = drawerIconOpacity,
                    valueRange = 0f..100f,
                    onValueChange = {
                        drawerIconOpacity = it
                        LauncherPreferences.DEFAULT_PREF.edit { putInt("drawer_pull_icon_opacity", it.toInt()) }
                        LauncherPreferences.PREF_DRAWER_PULL_ICON_OPACITY = it.toInt()
                    },
                    valueSuffix = "%"
                )
            }
            SettingsCard(position = CardPosition.MIDDLE, useSurface = true) {
                SettingsSwitchItem(
                    title = "Hold to move",
                    icon = Icons.Rounded.DragIndicator,
                    checked = drawerHoldToMove,
                    onCheckedChange = {
                        drawerHoldToMove = it
                        LauncherPreferences.DEFAULT_PREF.edit { putBoolean("drawer_pull_hold_to_move", it) }
                        LauncherPreferences.PREF_DRAWER_PULL_HOLD_TO_MOVE = it
                    }
                )
            }
            SettingsCard(position = CardPosition.MIDDLE, useSurface = true) {
                SettingsSwitchItem(
                    title = "Show background",
                    icon = Icons.Rounded.RadioButtonUnchecked,
                    checked = drawerBackground,
                    onCheckedChange = {
                        drawerBackground = it
                        LauncherPreferences.DEFAULT_PREF.edit { putBoolean("drawer_pull_background", it) }
                        LauncherPreferences.PREF_DRAWER_PULL_BACKGROUND = it
                    }
                )
            }

            SettingsCard(position = CardPosition.MIDDLE, useSurface = true) {
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    if (uri != null) {
                        val destination = File(Tools.DIR_DATA, "custom_drawer_icon.png")
                        try {
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                FileOutputStream(destination).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            LauncherPreferences.DEFAULT_PREF.edit { putString("drawer_pull_icon_path", destination.absolutePath) }
                            LauncherPreferences.PREF_DRAWER_PULL_ICON_PATH = destination.absolutePath
                            drawerIconPath = destination.absolutePath
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                SettingsActionItem(
                    title = "Change icon image",
                    summary = if (drawerIconPath != null) "Custom icon active" else "Default icon active",
                    icon = Icons.Rounded.AddPhotoAlternate,
                    onClick = { launcher.launch("image/*") }
                )
            }
            SettingsCard(position = CardPosition.BOTTOM, useSurface = true) {
                SettingsActionItem(
                    title = "Reset icon",
                    icon = Icons.Rounded.Restore,
                    onClick = {
                        LauncherPreferences.DEFAULT_PREF.edit { remove("drawer_pull_icon_path") }
                        LauncherPreferences.PREF_DRAWER_PULL_ICON_PATH = null
                        drawerIconPath = null
                    }
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
