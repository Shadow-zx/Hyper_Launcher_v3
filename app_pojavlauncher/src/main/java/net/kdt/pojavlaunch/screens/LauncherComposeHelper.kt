package net.kdt.pojavlaunch.screens

import android.widget.FrameLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.screens.theme.PojavTheme

object LauncherComposeHelper {
    private var settingsIconRes by mutableIntStateOf(R.drawable.ic_sharp_settings_24)

    interface OnFragmentViewCreatedListener {
        fun onCreated(view: FrameLayout)
    }

    @JvmStatic
    fun setSettingsIcon(iconRes: Int) {
        settingsIconRes = iconRes
    }

    @JvmStatic
    fun setContent(
        activity: FragmentActivity,
        onSettingsClick: Runnable,
        onFragmentViewCreated: OnFragmentViewCreatedListener
    ) {
        val composeView = ComposeView(activity).apply {
            setContent {
                PojavTheme {
                    PojavLauncherScreen(
                        settingsIconRes = settingsIconRes,
                        onSettingsClick = { onSettingsClick.run() },
                        onFragmentViewCreated = { onFragmentViewCreated.onCreated(it) }
                    )
                }
            }
        }
        activity.setContentView(composeView)
    }
}
