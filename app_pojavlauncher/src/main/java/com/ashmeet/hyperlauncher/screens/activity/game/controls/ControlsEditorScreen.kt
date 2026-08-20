package com.ashmeet.hyperlauncher.screens.activity.game.controls

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import com.ashmeet.hyperlauncher.components.SideNavigationRail
import com.ashmeet.hyperlauncher.dialog.EditControlSideDialog
import com.ashmeet.hyperlauncher.theme.PojavTheme
import kotlinx.coroutines.launch
import net.kdt.pojavlaunch.customcontrols.ControlLayout
import net.kdt.pojavlaunch.customcontrols.buttons.ControlInterface
import net.kdt.pojavlaunch.customcontrols.handleview.DrawerPullButton

@Composable
fun ControlsEditorScreen(
    controlLayout: ControlLayout,
    onDrawerButtonTap: (() -> Unit)? = null,
    onAction: (Int) -> Unit,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {
    val scope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    val editDialog = remember(controlLayout) {
        EditControlSideDialog(context, controlLayout)
    }

    LaunchedEffect(controlLayout) {
        controlLayout.setOnControlEditListener(object : ControlLayout.OnControlEditListener {
            override fun onEditControl(button: ControlInterface) {
                editDialog.setCurrentlyEditedButton(button)
                editDialog.adaptPanelPosition()
            }

            override fun onDisappearLayer(): Boolean {
                return editDialog.disappearLayer()
            }
        })
    }

    PojavTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        SideNavigationRail(
                            isEditor = true,
                            onAction = { action ->
                                onAction(action)
                                scope.launch { drawerState.close() }
                            },
                            isExport = true
                        )
                    }
                },
                gesturesEnabled = false,
                modifier = Modifier.fillMaxSize()
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        AndroidView(
                            factory = { controlLayout },
                            modifier = Modifier.fillMaxSize()
                        )

                        if (onDrawerButtonTap != null) {
                            AndroidView(
                                factory = { ctx ->
                                    // Use a match_parent container so the button can move freely
                                    FrameLayout(ctx).apply {
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                        val button = DrawerPullButton(ctx).apply {
                                            setOnClickListener { onDrawerButtonTap() }
                                        }
                                        addView(button)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        if (drawerState.isOpen) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        scope.launch { drawerState.close() }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
