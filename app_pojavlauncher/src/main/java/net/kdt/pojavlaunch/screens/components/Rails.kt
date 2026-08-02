package net.kdt.pojavlaunch.screens.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InstanceNavigationRail(
    onCreateNew: () -> Unit,
    onRefresh: () -> Unit,
    onImportModpack: () -> Unit,
    onBack: () -> Unit
) {
    NavigationRail(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxHeight(),
        header = {
            SidebarRailButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                label = "Back",
                onClick = onBack
            )
        }
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        SidebarRailButton(
            icon = Icons.Rounded.Add,
            label = "New",
            onClick = onCreateNew,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        SidebarRailButton(
            icon = Icons.Rounded.Refresh,
            label = "Refresh",
            onClick = onRefresh
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        SidebarRailButton(
            icon = Icons.Rounded.Search,
            label = "Import",
            onClick = onImportModpack
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
