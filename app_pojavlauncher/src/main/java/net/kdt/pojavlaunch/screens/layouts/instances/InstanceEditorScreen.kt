package net.kdt.pojavlaunch.screens.layouts.instances

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.multirt.Runtime
import net.kdt.pojavlaunch.screens.components.DefaultSwitch
import net.kdt.pojavlaunch.screens.components.MineButton
import net.kdt.pojavlaunch.screens.utils.rememberDrawablePainter

@Composable
fun InstanceEditorScreen(
    instanceName: String,
    onInstanceNameChange: (String) -> Unit,
    versionId: String,
    onSelectVersion: () -> Unit,
    controlLayout: String,
    onSelectControl: () -> Unit,
    sharedData: Boolean,
    onSharedDataChange: (Boolean) -> Unit,
    jvmArgs: String,
    onJvmArgsChange: (String) -> Unit,
    selectedRuntime: Runtime?,
    runtimes: List<Runtime>,
    onRuntimeSelected: (Runtime) -> Unit,
    selectedRenderer: String,
    renderers: List<String>,
    rendererDisplayNames: List<String>,
    onRendererSelected: (String) -> Unit,
    instanceIcon: Drawable?,
    onChangeIcon: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { onChangeIcon() },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Image(
                        painter = rememberDrawablePainter(instanceIcon),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                    )
                    
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                // Instance Name
                OutlinedTextField(
                    value = instanceName,
                    onValueChange = onInstanceNameChange,
                    label = { Text(stringResource(R.string.profiles_profile_name)) },
                    placeholder = { Text(stringResource(R.string.unnamed)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Version Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = versionId,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.profiles_profile_version)) },
                        placeholder = { Text(stringResource(R.string.version_select_hint)) },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                             IconButton(onClick = onSelectVersion) {
                                 Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null)
                             }
                        }
                    )
                    MineButton(
                        text = stringResource(R.string.global_select),
                        onClick = onSelectVersion,
                        modifier = Modifier.height(40.dp)
                    )
                }

                // Control Layout Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = controlLayout,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.default_control)) },
                        placeholder = { Text(stringResource(R.string.use_global_default)) },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = onSelectControl) {
                                Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null)
                            }
                        }
                    )
                    MineButton(
                        text = stringResource(R.string.global_select),
                        onClick = onSelectControl,
                        modifier = Modifier.height(40.dp)
                    )
                }

                // Shared Data Toggle
                ListItem(
                    headlineContent = { Text(stringResource(R.string.instance_shared_data)) },
                    supportingContent = { 
                        Text(stringResource(if (sharedData) R.string.instance_shared_data_on else R.string.instance_shared_data_off)) 
                    },
                    trailingContent = {
                        DefaultSwitch(
                            checked = sharedData,
                            onCheckedChange = onSharedDataChange
                        )
                    },
                    modifier = Modifier.clickable { onSharedDataChange(!sharedData) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                // JVM Arguments
                OutlinedTextField(
                    value = jvmArgs,
                    onValueChange = onJvmArgsChange,
                    label = { Text(stringResource(R.string.pvc_jvmArgs)) },
                    placeholder = { Text(stringResource(R.string.use_global_default)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Runtime Selection
                InstanceDropdown(
                    label = stringResource(R.string.pedit_java_runtime),
                    items = runtimes,
                    selectedItem = selectedRuntime,
                    itemLabel = { 
                        if (runtimes.indexOf(it) == runtimes.size - 1) it.name
                        else "${it.name.replace(".tar.xz", "")} - ${it.versionString ?: stringResource(R.string.multirt_runtime_corrupt)}"
                    },
                    onItemSelected = onRuntimeSelected
                )

                // Renderer Selection
                InstanceDropdown(
                    label = stringResource(R.string.pedit_renderer),
                    items = renderers,
                    selectedItem = selectedRenderer,
                    itemLabel = { 
                        val index = renderers.indexOf(it)
                        if (index != -1 && index < rendererDisplayNames.size) rendererDisplayNames[index]
                        else it
                    },
                    onItemSelected = onRendererSelected
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Footer Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MineButton(
                        text = stringResource(R.string.global_save),
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        height = 48.dp
                    )
                    
                    MineButton(
                        text = stringResource(R.string.global_delete),
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        height = 48.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> InstanceDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    itemLabel: @Composable (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedItem?.let { itemLabel(it) } ?: "",
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
        
        // Transparent overlay to catch clicks
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
