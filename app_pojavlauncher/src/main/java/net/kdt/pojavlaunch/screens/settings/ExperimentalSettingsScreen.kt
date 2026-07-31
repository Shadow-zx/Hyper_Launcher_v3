package net.kdt.pojavlaunch.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.screens.settings.preferences.CardPosition
import net.kdt.pojavlaunch.screens.settings.preferences.PreferenceCategory
import net.kdt.pojavlaunch.screens.settings.preferences.SettingsCard
import net.kdt.pojavlaunch.screens.settings.preferences.SettingsScreenWrapper
import net.kdt.pojavlaunch.screens.settings.preferences.SettingsSwitchItem

@Composable
fun ExperimentalSettingsScreen(
    onBack: () -> Unit,
    isFreedrenoAvailable: Boolean
) {
    val context = LocalContext.current
    var dumpShaders by remember { mutableStateOf(LauncherPreferences.PREF_DUMP_SHADERS) }
    var bigCoreAffinity by remember { mutableStateOf(LauncherPreferences.PREF_BIG_CORE_AFFINITY) }
    var freedrenoSysmem by remember { mutableStateOf(LauncherPreferences.PREF_FREEDRENO_SYSMEM) }
    var alsoftForceOpenSL by remember { mutableStateOf(LauncherPreferences.PREF_ALSOFT_FORCE_OPENSL) }

    SettingsScreenWrapper(
        title = stringResource(R.string.preference_experimental_title),
        onBack = onBack
    ) {
        PreferenceCategory(title = stringResource(R.string.preference_category_experimental_settings))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SettingsCard(position = CardPosition.TOP, useSurface = true) {
                SettingsSwitchItem(
                    title = stringResource(R.string.preference_shader_dump_title),
                    summary = stringResource(R.string.preference_shader_dump_description),
                    icon = Icons.Default.Settings,
                    checked = dumpShaders,
                    warningTooltip = "Experimental: Shader dumping can significantly impact performance and fill up storage space.",
                    onCheckedChange = {
                        dumpShaders = it
                        LauncherPreferences.DEFAULT_PREF.edit().putBoolean("dump_shaders", it).apply()
                        LauncherPreferences.loadPreferences(context)
                    }
                )
            }

            val bigCorePos = if (isFreedrenoAvailable) CardPosition.MIDDLE else CardPosition.BOTTOM
            SettingsCard(position = bigCorePos, useSurface = true) {
                SettingsSwitchItem(
                    title = stringResource(R.string.preference_force_big_core_title),
                    summary = stringResource(R.string.preference_force_big_core_desc),
                    icon = Icons.Default.Settings,
                    checked = bigCoreAffinity,
                    warningTooltip = "Experimental: Forcing big core affinity may lead to increased heat and battery drain.",
                    onCheckedChange = {
                        bigCoreAffinity = it
                        LauncherPreferences.DEFAULT_PREF.edit().putBoolean("bigCoreAffinity", it).apply()
                        LauncherPreferences.loadPreferences(context)
                    }
                )
            }

            if (isFreedrenoAvailable) {
                SettingsCard(position = CardPosition.MIDDLE, useSurface = true) {
                    SettingsSwitchItem(
                        title = stringResource(R.string.preference_sysmem_title),
                        summary = stringResource(R.string.preference_sysmem_summary),
                        icon = Icons.Default.Settings,
                        checked = freedrenoSysmem,
                        warningTooltip = "Experimental: Freedreno System Memory rendering can be unstable and cause crashes on certain drivers.",
                        onCheckedChange = {
                            freedrenoSysmem = it
                            LauncherPreferences.DEFAULT_PREF.edit().putBoolean("freedrenoSysmem", it).apply()
                            LauncherPreferences.loadPreferences(context)
                        }
                    )
                }
            }

            SettingsCard(position = if (isFreedrenoAvailable) CardPosition.BOTTOM else CardPosition.SINGLE, useSurface = true) {
                SettingsSwitchItem(
                    title = stringResource(R.string.preference_alsoft_opensl_title),
                    summary = stringResource(R.string.preference_alsoft_opensl_summary),
                    icon = ImageVector.vectorResource(R.drawable.ic_px_dynamic),
                    checked = alsoftForceOpenSL,
                    warningTooltip = "Experimental: Forcing OpenSL may solve audio issues on some devices but could cause latency or crashes on others.",
                    onCheckedChange = {
                        alsoftForceOpenSL = it
                        LauncherPreferences.DEFAULT_PREF.edit().putBoolean("alsoftForceOpenSL", it).apply()
                        LauncherPreferences.loadPreferences(context)
                    }
                )
            }
        }
    }
}
