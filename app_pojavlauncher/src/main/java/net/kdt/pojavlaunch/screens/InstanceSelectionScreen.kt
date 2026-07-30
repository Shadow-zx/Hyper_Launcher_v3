package net.kdt.pojavlaunch.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.kdt.pojavlaunch.instances.DisplayInstance
import net.kdt.pojavlaunch.instances.Instances
import net.kdt.pojavlaunch.screens.components.InstanceListItem

@Composable
fun InstanceSelectionScreen(
    onBack: () -> Unit,
    onCreateNew: () -> Unit,
    onImportModpack: () -> Unit,
    onEditInstance: (DisplayInstance) -> Unit,
    onRenameInstance: (DisplayInstance, onRefresh: () -> Unit) -> Unit,
    onDeleteInstance: (DisplayInstance, onRefresh: () -> Unit) -> Unit
) {
    var instances by remember { mutableStateOf<List<DisplayInstance>>(emptyList()) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) } 
    var refreshKey by remember { mutableIntStateOf(0) }

    val loadInstances = {
        isLoading = true
        net.kdt.pojavlaunch.PojavApplication.sExecutorService.execute {
            try {
                val loaded = Instances.loadDisplay()
                instances = loaded.list
                selectedIndex = loaded.selectedIndex
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
    }

    LaunchedEffect(refreshKey) {
        loadInstances()
    }

    val filteredInstances = remember(instances, selectedTab) {
        when (selectedTab) {
            1 -> instances.filter { isVanilla(it.versionId) }
            2 -> instances.filter { !isVanilla(it.versionId) }
            else -> instances
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar using NavigationRail
            InstanceNavigationRail(
                onCreateNew = onCreateNew,
                onRefresh = { loadInstances() },
                onImportModpack = onImportModpack,
                onBack = onBack
            )

            // Main Content Area wrapped in a Rounded Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // M3 Tabs at the top edges of the card
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    height = 3.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    ) {
                        val tabs = listOf("All", "Vanilla", "Modded")
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                interactionSource = remember { MutableInteractionSource() },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Instances in a List (Vertical Scroll, One Column)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredInstances) { instance ->
                                val actualIndex = instances.indexOf(instance)
                                val isSelected = actualIndex == selectedIndex
                                InstanceListItem(
                                    instance = instance,
                                    isSelected = isSelected,
                                    onClick = {
                                        Instances.setSelectedInstance(instance)
                                        selectedIndex = actualIndex
                                    },
                                    onEdit = { onEditInstance(instance) },
                                    onRename = { onRenameInstance(instance) { refreshKey++ } },
                                    onDelete = { onDeleteInstance(instance) { refreshKey++ } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

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

@Composable
fun SidebarRailButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
        )
    }
}

private fun isVanilla(versionId: String?): Boolean {
    if (versionId == null) return true
    val lower = versionId.lowercase()
    return !lower.contains("fabric") && 
           !lower.contains("forge") && 
           !lower.contains("quilt") && 
           !lower.contains("optifine") &&
           !lower.contains("neoforge") &&
           !lower.contains("bta")
}
