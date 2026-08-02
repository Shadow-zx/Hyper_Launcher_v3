package net.kdt.pojavlaunch.screens.layouts

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.screens.compose.AccountSpinnerCompose
import net.kdt.pojavlaunch.screens.compose.ProgressLayoutCompose


@Composable
fun PojavLauncherScreen(
    settingsIconRes: Int,
    isFileManagerVisible: Boolean,
    onSettingsClick: () -> Unit,
    onContentInstallerClick: () -> Unit,
    onInstanceDirectoryClick: () -> Unit,
    onFragmentViewCreated: (FrameLayout) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .zIndex(1f)
            ) {
                AccountSpinnerCompose(
                    modifier = Modifier.fillMaxSize()
                )
                
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    if (isFileManagerVisible) {
                        IconButton(
                            onClick = onContentInstallerClick,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = "Content Installer",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = onInstanceDirectoryClick,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Folder,
                                contentDescription = "Instance Directory",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = settingsIconRes),
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AndroidView(
                    factory = { context ->
                        FrameLayout(context).apply {
                            id = R.id.container_fragment
                            onFragmentViewCreated(this)
                        }
                    },
                    update = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            ProgressLayoutCompose()
        }
    }
}
