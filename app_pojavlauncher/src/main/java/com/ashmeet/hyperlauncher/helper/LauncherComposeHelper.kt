package com.ashmeet.hyperlauncher.helper

import android.widget.FrameLayout
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import com.ashmeet.hyperlauncher.components.SideNavigationRail
import com.ashmeet.hyperlauncher.screens.activity.game.BaseMainScreen
import com.ashmeet.hyperlauncher.screens.activity.game.ExitScreen
import com.ashmeet.hyperlauncher.screens.activity.game.LoggerView
import com.ashmeet.hyperlauncher.screens.activity.launcher.PojavLauncherScreen
import com.ashmeet.hyperlauncher.theme.PojavTheme
import kotlinx.coroutines.launch
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.customcontrols.ControlLayout

object LauncherComposeHelper {
    private var settingsIconRes: Int by mutableIntStateOf(R.drawable.ic_sharp_settings_24)
    private var mIsFileManagerVisible by mutableStateOf(true)

    interface OnFragmentViewCreatedListener {
        fun onCreated(view: FrameLayout)
    }

    interface DrawerController {
        fun open()
        fun close()
        fun toggle()
        fun isOpen(): Boolean
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

    @JvmStatic
    fun setBaseMainContent(
        composeView: ComposeView,
        isInEditor: Boolean,
        controlLayout: ControlLayout,
        loggerView: LoggerView,
        onDrawerControllerCreated: (DrawerController) -> Unit,
        onAction: (Int) -> Unit
    ) {
        composeView.setContent {
            PojavTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                onDrawerControllerCreated(object : DrawerController {
                    override fun open() { scope.launch { drawerState.open() } }
                    override fun close() { scope.launch { drawerState.close() } }
                    override fun toggle() {
                        scope.launch {
                            if (drawerState.isOpen) drawerState.close()
                            else drawerState.open()
                        }
                    }
                    override fun isOpen(): Boolean = drawerState.isOpen
                })

                BaseMainScreen(
                    drawerState = drawerState,
                    controlLayout = controlLayout,
                    loggerView = loggerView,
                    drawerContent = {
                        SideNavigationRail(isEditor = isInEditor, onAction = onAction, isExport = false)
                    }
                )
            }
        }
    }
}
