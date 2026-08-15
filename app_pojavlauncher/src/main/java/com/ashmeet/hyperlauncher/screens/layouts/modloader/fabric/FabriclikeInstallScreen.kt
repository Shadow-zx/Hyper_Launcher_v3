package com.ashmeet.hyperlauncher.screens.layouts.modloader.fabric

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.modloaders.FabricVersion
import com.ashmeet.hyperlauncher.components.DefaultSwitch

@Composable
fun FabriclikeInstallScreen(
    title: String,
    loaderName: String,
    isLoading: Boolean,
    isInstalling: Boolean,
    gameVersions: List<FabricVersion>,
    loaderVersions: List<FabricVersion>,
    onBack: () -> Unit,
    onInstall: (gameVersion: String, loaderVersion: String) -> Unit
) {
    var selectedGameVersion by remember { mutableStateOf<FabricVersion?>(null) }
    var selectedLoaderVersion by remember { mutableStateOf<FabricVersion?>(null) }
    var onlyStable by remember { mutableStateOf(true) }

    val filteredGameVersions = remember(gameVersions, onlyStable) {
        if (onlyStable) gameVersions.filter { it.stable } else gameVersions
    }
    
    val filteredLoaderVersions = remember(loaderVersions, onlyStable) {
        if (onlyStable) loaderVersions.filter { it.stable } else loaderVersions
    }

    LaunchedEffect(filteredGameVersions) {
        if (selectedGameVersion == null && filteredGameVersions.isNotEmpty()) {
            selectedGameVersion = filteredGameVersions.first()
        }
    }

    LaunchedEffect(filteredLoaderVersions) {
        if (selectedLoaderVersion == null && filteredLoaderVersions.isNotEmpty()) {
            selectedLoaderVersion = filteredLoaderVersions.first()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(android.R.string.cancel)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 16.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outline
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Column: Version selection
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    VersionSpinner(
                        label = stringResource(R.string.fabric_dl_game_version),
                        versions = filteredGameVersions,
                        selectedVersion = selectedGameVersion,
                        onVersionSelected = { selectedGameVersion = it }
                    )

                    VersionSpinner(
                        label = stringResource(R.string.fabric_dl_loader_version, loaderName),
                        versions = filteredLoaderVersions,
                        selectedVersion = selectedLoaderVersion,
                        onVersionSelected = { selectedLoaderVersion = it }
                    )
                }

                // Right Column: Toggle and Install Button
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onlyStable = !onlyStable }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.fabric_dl_only_stable),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        DefaultSwitch(
                            checked = onlyStable,
                            onCheckedChange = { onlyStable = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val gv = selectedGameVersion?.version
                            val lv = selectedLoaderVersion?.version
                            if (gv != null && lv != null) {
                                onInstall(gv, lv)
                            }
                        },
                        enabled = !isLoading && !isInstalling && selectedGameVersion != null && selectedLoaderVersion != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (isInstalling) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.global_save))
                        }
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun VersionSpinner(
    label: String,
    versions: List<FabricVersion>,
    selectedVersion: FabricVersion?,
    onVersionSelected: (FabricVersion) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedVersion?.version ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = true,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        
        // Transparent overlay to catch clicks as OutlinedTextField might swallow them
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.4f) // Align with column width
        ) {
            versions.forEach { version ->
                DropdownMenuItem(
                    text = { Text(version.version) },
                    onClick = {
                        onVersionSelected(version)
                        expanded = false
                    }
                )
            }
        }
    }
}
