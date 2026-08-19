package com.ashmeet.hyperlauncher.screens.activity.game.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ashmeet.hyperlauncher.components.SideNavigationRail
import com.ashmeet.hyperlauncher.theme.PojavTheme
import kotlinx.coroutines.launch
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.customcontrols.ControlLayout

@Composable
fun ControlsEditorScreen(
    controlLayout: ControlLayout,
    onDrawerButtonTap: () -> Unit,
    onAction: (Int) -> Unit,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {
    val scope = rememberCoroutineScope()
    
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
                        // The main editor area
                        AndroidView(
                            factory = { controlLayout },
                            modifier = Modifier.fillMaxSize()
                        )

                        // The "Hint" text
                        Text(
                            text = stringResource(id = R.string.hint_control_mapping),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 24.dp, end = 16.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        // Compose version of DrawerPullButton
                        IconButton(
                            onClick = onDrawerButtonTap,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                                .size(48.dp)
                                .background(Color.Black.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "Menu",
                                tint = Color.White
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
