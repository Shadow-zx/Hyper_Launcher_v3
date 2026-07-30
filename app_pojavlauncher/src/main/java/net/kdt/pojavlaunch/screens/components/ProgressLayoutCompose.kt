package net.kdt.pojavlaunch.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.ashmeet.hyperlauncher.R
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper
import net.kdt.pojavlaunch.progresskeeper.ProgressListener
import net.kdt.pojavlaunch.progresskeeper.TaskCountListener

data class TaskProgressState(
    val key: String,
    val progress: Int = 0,
    val message: String = ""
)

@Composable
fun ProgressLayoutCompose(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var taskCount by remember { mutableIntStateOf(ProgressKeeper.getTaskCount()) }
    val activeTasks = remember { mutableStateMapOf<String, TaskProgressState>() }
    var expanded by remember { mutableStateOf(false) }

    val observedKeys = listOf(
        "unpack_runtime", "download_minecraft", "download_verlist",
        "authenticate", "install_modpack", "extract_components",
        "extract_single_files", "instance_install", "data_migration"
    )

    DisposableEffect(Unit) {
        val taskCountListener = TaskCountListener { tc ->
            taskCount = tc
            false
        }
        ProgressKeeper.addTaskCountListener(taskCountListener)

        val listeners = observedKeys.map { key ->
            val listener = object : ProgressListener {
                override fun onProgressStarted() {
                    activeTasks[key] = TaskProgressState(key)
                }

                override fun onProgressUpdated(progress: Int, resid: Int, vararg va: Any?) {
                    val msg = if (resid != -1) context.getString(resid, *va)
                    else if (va.isNotEmpty() && va[0] != null) va[0].toString()
                    else ""
                    activeTasks[key] = TaskProgressState(key, progress, msg)
                }

                override fun onProgressEnded() {
                    activeTasks.remove(key)
                }
            }
            ProgressKeeper.addListener(key, listener)
            key to listener
        }

        onDispose {
            ProgressKeeper.removeTaskCountListener(taskCountListener)
            listeners.forEach { (key, listener) ->
                ProgressKeeper.removeListener(key, listener)
            }
        }
    }

    if (taskCount > 0) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // background_bottom_bar
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.progresslayout_tasks_in_progress, taskCount),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(activeTasks.values.toList()) { task ->
                        TaskItem(task)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: TaskProgressState) {
    Column {
        Text(
            text = task.message,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (task.progress >= 0) {
            LinearProgressIndicator(
                progress = { task.progress / 100f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline,
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
