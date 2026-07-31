package net.kdt.pojavlaunch.screens

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.zIndex
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
import net.kdt.pojavlaunch.screens.components.AccountSpinnerCompose
import net.kdt.pojavlaunch.screens.components.ProgressLayoutCompose

@Composable
fun PojavLauncherScreen(
    settingsIconRes: Int,
    onSettingsClick: () -> Unit,
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
                
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(id = settingsIconRes),
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
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
