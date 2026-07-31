package net.kdt.pojavlaunch.prefs.screens

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.screens.settings.ExperimentalSettingsScreen
import net.kdt.pojavlaunch.screens.theme.PojavTheme
import net.kdt.pojavlaunch.utils.GLInfoUtils

class LauncherPreferenceExperimentalFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val hasFreedreno = GLInfoUtils.getGlInfo().isAdreno
        return ComposeView(requireContext()).apply {
            setContent {
                PojavTheme {
                    ExperimentalSettingsScreen(
                        onBack = { requireActivity().onBackPressed() },
                        isFreedrenoAvailable = hasFreedreno
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LauncherPreferences.DEFAULT_PREF?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        LauncherPreferences.DEFAULT_PREF?.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        LauncherPreferences.loadPreferences(context)
    }
}
