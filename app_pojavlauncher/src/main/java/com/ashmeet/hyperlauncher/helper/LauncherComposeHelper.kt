package com.ashmeet.hyperlauncher.helper

import android.widget.FrameLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import net.ashmeet.hyperlauncher.R
import com.ashmeet.hyperlauncher.components.SideNavigationRail
import com.ashmeet.hyperlauncher.screens.activity.PojavLauncherScreen
import com.ashmeet.hyperlauncher.theme.PojavTheme
import com.ashmeet.hyperlauncher.screens.activity.ExitScreen

object LauncherComposeHelper {
    private var settingsIconRes: Int by mutableIntStateOf(R.drawable.ic_sharp_settings_24)
    private var mIsFileManagerVisible by mutableStateOf(true)

    interface OnFragmentViewCreatedListener {
        fun onCreated(view: FrameLayout)
    }

    @JvmStatic
    fun setMainDrawerContent(
        composeView: ComposeView,
        isInEditor: Boolean,
        isExport: Boolean,
        onAction: (Int) -> Unit
    ) {
        composeView.setContent {
            PojavTheme {
                SideNavigationRail(isEditor = isInEditor, onAction = onAction, isExport = isExport)
            }
        }
    }

    @JvmStatic
    fun setSettingsIcon(iconRes: Int) {
        settingsIconRes = iconRes
    }

    @JvmStatic
    fun setFileManagerVisible(visible: Boolean) {
        mIsFileManagerVisible = visible
    }

    @JvmStatic
    fun setExitContent(
        composeView: ComposeView,
        title: String,
        logs: String,
        onShareClick: () -> Unit,
        onCopyClick: () -> Unit,
        onRestartClick: () -> Unit,
        onOpenCrashReport: (String) -> Unit
    ) {
        composeView.setContent {
            PojavTheme {
                ExitScreen(
                    title = title,
                    logs = logs,
                    onShareClick = onShareClick,
                    onCopyClick = onCopyClick,
                    onRestartClick = onRestartClick,
                    onOpenCrashReport = onOpenCrashReport
                )
            }
        }
    }

    @JvmStatic
    fun setContent(
        activity: FragmentActivity,
        onSettingsClick: Runnable,
        onContentInstallerClick: Runnable,
        onInstanceDirectoryClick: Runnable,
        onFragmentViewCreated: OnFragmentViewCreatedListener
    ) {
        val composeView = ComposeView(activity).apply {
            setContent {
                PojavTheme {
                    PojavLauncherScreen(
                        settingsIconRes = settingsIconRes,
                        isFileManagerVisible = mIsFileManagerVisible,
                        onSettingsClick = { onSettingsClick.run() },
                        onContentInstallerClick = { onContentInstallerClick.run() },
                        onInstanceDirectoryClick = { onInstanceDirectoryClick.run() },
                        onFragmentViewCreated = { onFragmentViewCreated.onCreated(it) }
                    )
                }
            }
        }
        activity.setContentView(composeView)
    }
}