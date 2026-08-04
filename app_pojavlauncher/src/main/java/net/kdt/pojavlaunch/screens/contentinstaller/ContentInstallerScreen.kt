package net.kdt.pojavlaunch.screens.contentinstaller

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.kdt.pojavlaunch.screens.components.SidebarRailButton
import net.kdt.pojavlaunch.screens.theme.PojavTheme
import net.kdt.pojavlaunch.screens.contentinstaller.models.ContentInstallerType
import net.kdt.pojavlaunch.screens.contentinstaller.models.ContentSource
import net.kdt.pojavlaunch.screens.contentinstaller.models.ModrinthProject
import net.kdt.pojavlaunch.screens.contentinstaller.models.ModrinthVersion
import net.kdt.pojavlaunch.screens.contentinstaller.components.ProjectDetailsSidebar
import net.kdt.pojavlaunch.screens.contentinstaller.components.ProjectItemView
import net.kdt.pojavlaunch.screens.contentinstaller.components.SearchFiltersSidebar
import net.kdt.pojavlaunch.screens.contentinstaller.components.SubVersionItemView
import net.kdt.pojavlaunch.screens.contentinstaller.components.VersionItemView

@Composable
fun ContentInstallerScreen(
    onBack: () -> Unit,
    onSearch: (String, ContentInstallerType, version: String?, loader: String?, source: ContentSource) -> Unit,
    onProjectClick: (ModrinthProject) -> Unit,
    onVersionClick: (ModrinthVersion) -> Unit,
    onRefresh: () -> Unit,
    onImportModpack: () -> Unit,
    projects: List<ModrinthProject>,
    isLoading: Boolean,
    selectedVersion: String?,
    selectedLoader: String?,
    instanceVersion: String?,
    instanceLoader: String?,
    viewingProject: ModrinthProject? = null,
    selectedType: ContentInstallerType = ContentInstallerType.MODS,
    selectedSource: ContentSource = ContentSource.MODRINTH,
    projectVersions: List<ModrinthVersion> = emptyList(),
    availableProjectMCVersions: List<String> = emptyList(),
    selectedProjectMCVersion: String? = null,
    onProjectMCVersionClick: (String) -> Unit = {},
    onBackToProjects: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = Color.Transparent,
                modifier = Modifier.fillMaxHeight(),
                header = {
                    SidebarRailButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        label = "Back",
                        onClick = {
                            if (isSearchActive) {
                                isSearchActive = false
                                searchQuery = ""
                                onSearch(
                                    "",
                                    selectedType,
                                    selectedVersion,
                                    selectedLoader,
                                    selectedSource
                                )
                            } else if (viewingProject != null) {
                                onBackToProjects()
                            } else {
                                onBack()
                            }
                        }
                    )
                }
            ) {
                Spacer(modifier = Modifier.weight(1f))

                SidebarRailButton(
                    icon = Icons.Rounded.Add,
                    label = "New",
                    onClick = onImportModpack,
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
                    label = "Search",
                    onClick = { isSearchActive = !isSearchActive }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(top = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = isSearchActive,
                        transitionSpec = {
                            (scaleIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                initialScale = 0.9f
                            ) + fadeIn()) togetherWith fadeOut(animationSpec = tween(200))
                        },
                        label = "search_transition"
                    ) { active ->
                        if (active) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                    onSearch(it, selectedType, selectedVersion, selectedLoader, selectedSource)
                                },
                                enabled = viewingProject == null,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                placeholder = { Text("Search content...") },
                                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        onSearch(searchQuery, selectedType, selectedVersion, selectedLoader, selectedSource)
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        } else {
                            TabRow(
                                selectedTabIndex = ContentInstallerType.entries.indexOf(selectedType),
                                containerColor = Color.Transparent,
                                divider = {},
                                indicator = { tabPositions ->
                                    val index = ContentInstallerType.entries.indexOf(selectedType)
                                    if (index >= 0 && index < tabPositions.size) {
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                            height = 3.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            ) {
                                ContentInstallerType.entries.forEach { type ->
                                    Tab(
                                        selected = selectedType == type,
                                        onClick = {
                                            if (selectedType != type) {
                                                if (viewingProject != null) onBackToProjects()
                                                onSearch(searchQuery, type, selectedVersion, selectedLoader, selectedSource)
                                            }
                                        },
                                        interactionSource = remember { MutableInteractionSource() },
                                        text = {
                                            Text(
                                                text = stringResource(type.labelRes),
                                                fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    AnimatedContent(
                        targetState = isLoading to viewingProject,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                        },
                        label = "content_transition",
                        modifier = Modifier.weight(1f)
                    ) { (loading, project) ->
                        if (loading && projects.isEmpty() && project == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else if (project != null) {
                            VersionList(
                                projectVersions = projectVersions,
                                availableProjectMCVersions = availableProjectMCVersions,
                                selectedProjectMCVersion = selectedProjectMCVersion,
                                instanceVersion = instanceVersion,
                                instanceLoader = instanceLoader,
                                isLoading = loading,
                                onProjectMCVersionClick = onProjectMCVersionClick,
                                onVersionClick = onVersionClick
                            )
                        } else if (projects.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Rounded.Warning,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (searchQuery.isNotEmpty()) "No results found" else "Search to find content",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    TextButton(
                                        onClick = onRefresh,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Refresh")
                                    }
                                }
                            }
                        } else {
                            val lazyListState = rememberLazyListState()
                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(projects, key = { it.id }) { p ->
                                    ProjectItemView(
                                        project = p,
                                        onClick = { onProjectClick(p) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Surface(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
                    .padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                tonalElevation = 4.dp
            ) {
                if (viewingProject != null) {
                    ProjectDetailsSidebar(viewingProject)
                } else {
                    SearchFiltersSidebar(
                        instanceVersion = instanceVersion,
                        instanceLoader = instanceLoader,
                        selectedVersion = selectedVersion,
                        selectedLoader = selectedLoader,
                        selectedSource = selectedSource,
                        showLoaderFilter = (selectedType == ContentInstallerType.MODS || selectedType == ContentInstallerType.MODPACKS),
                        onVersionChange = { onSearch(searchQuery, selectedType, it, selectedLoader, selectedSource) },
                        onLoaderChange = { onSearch(searchQuery, selectedType, selectedVersion, it, selectedSource) },
                        onSourceChange = { onSearch(searchQuery, selectedType, selectedVersion, selectedLoader, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionList(
    projectVersions: List<ModrinthVersion>,
    availableProjectMCVersions: List<String>,
    selectedProjectMCVersion: String?,
    instanceVersion: String?,
    instanceLoader: String?,
    isLoading: Boolean = false,
    onProjectMCVersionClick: (String) -> Unit,
    onVersionClick: (ModrinthVersion) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading && projectVersions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (selectedProjectMCVersion == null) {
            Text(
                text = "Select Game Version",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            val lazyListState = rememberLazyListState()
            LaunchedEffect(availableProjectMCVersions, instanceVersion) {
                if (availableProjectMCVersions.isNotEmpty()) {
                    val compatibleIndex = availableProjectMCVersions.indexOfFirst { v ->
                        instanceVersion != null && (instanceVersion.contains(v) || v.contains(instanceVersion))
                    }
                    if (compatibleIndex > 5) {
                        delay(200)
                        lazyListState.animateScrollToItem(compatibleIndex)
                    }
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableProjectMCVersions, key = { it }) { v ->
                    val isCompatible = instanceVersion != null && (instanceVersion.contains(v) || v.contains(instanceVersion))
                    SubVersionItemView(
                        text = v,
                        isCompatible = isCompatible,
                        onClick = { onProjectMCVersionClick(v) }
                    )
                }
            }
        } else {
            val filteredVersions = remember(projectVersions, selectedProjectMCVersion) {
                projectVersions.filter { it.gameVersions.contains(selectedProjectMCVersion) }
            }
            
            val lazyListState = rememberLazyListState()
            LaunchedEffect(filteredVersions, instanceVersion, instanceLoader) {
                if (filteredVersions.isNotEmpty()) {
                    val compatibleIndex = filteredVersions.indexOfFirst { version ->
                        instanceVersion != null && version.gameVersions.any { it.contains(instanceVersion) || instanceVersion.contains(it) } &&
                                (instanceLoader == null || version.loaders.any { it.equals(instanceLoader, ignoreCase = true) })
                    }
                    if (compatibleIndex > 5) {
                        delay(200)
                        lazyListState.animateScrollToItem(compatibleIndex)
                    }
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredVersions, key = { it.id }) { version ->
                    val isMCCompatible = instanceVersion != null && version.gameVersions.any { it.contains(instanceVersion) || instanceVersion.contains(it) }
                    val isLoaderCompatible = instanceLoader == null || version.loaders.any { it.equals(instanceLoader, ignoreCase = true) }
                    
                    VersionItemView(
                        version = version,
                        isCompatible = isMCCompatible && isLoaderCompatible,
                        isLoaderCompatible = isLoaderCompatible,
                        onClick = { onVersionClick(version) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun ContentInstallerScreenPreview() {
    val sampleProject = ModrinthProject(
        id = "1",
        title = "Sample Mod",
        description = "A sample mod for testing the installer screen.",
        iconUrl = null,
        fullDescription = "This is a detailed description of the sample mod."
    )

    PojavTheme {
        ContentInstallerScreen(
            onBack = {},
            onSearch = { _, _, _, _, _ -> },
            onProjectClick = {},
            onVersionClick = {},
            onRefresh = {},
            onImportModpack = {},
            projects = listOf(
                sampleProject,
                sampleProject.copy(id = "2", title = "Another Mod", description = "Description for the second mod.")
            ),
            isLoading = false,
            selectedVersion = "1.20.1",
            selectedLoader = "fabric",
            instanceVersion = "1.20.1",
            instanceLoader = "fabric"
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun ContentInstallerScreenDetailPreview() {
    val sampleProject = ModrinthProject(
        id = "1",
        title = "Sample Mod",
        description = "A sample mod for testing the installer screen.",
        iconUrl = null,
        fullDescription = "This is a detailed description of the sample mod."
    )

    val sampleVersion = ModrinthVersion(
        id = "v1",
        name = "1.0.0",
        gameVersions = listOf("1.20.1"),
        loaders = listOf("fabric"),
        downloadUrl = "https://example.com"
    )

    PojavTheme {
        ContentInstallerScreen(
            onBack = {},
            onSearch = { _, _, _, _, _ -> },
            onProjectClick = {},
            onVersionClick = {},
            onRefresh = {},
            onImportModpack = {},
            projects = emptyList(),
            isLoading = false,
            selectedVersion = "1.20.1",
            selectedLoader = "fabric",
            instanceVersion = "1.20.1",
            instanceLoader = "fabric",
            viewingProject = sampleProject,
            projectVersions = listOf(sampleVersion),
            availableProjectMCVersions = listOf("1.20.1", "1.19.2"),
            selectedProjectMCVersion = "1.20.1"
        )
    }
}

